package com.lge.launcher3.allapps;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.launcher3.Alarm;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeleteDropTarget;
import com.android.launcher3.DragScroller;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.FocusIndicatorView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherState;
import com.android.launcher3.OnAlarmListener;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Workspace;
import com.android.launcher3.allapps.PersonalWorkSlidingTabStrip;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.folder.FolderPagedView;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.lge.launcher3.PagedView;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsConstant;
import com.lge.launcher3.allapps.AllAppsSearchUtil;
import com.lge.launcher3.allapps.AllAppsSort;
import com.lge.launcher3.allapps.AllAppsSortDialog;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.pageindicator.PageIndicatorExtension;
import com.lge.launcher3.profile.LGInvariantDeviceProfile;
import com.lge.launcher3.screeneffect.ScreenEffectConst;
import com.lge.launcher3.screeneffect.ScreenEffectManager;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.CPUBoostService;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.OrientationUtils;
import com.lge.launcher3.util.PackageUtils;
import com.lge.launcher3.util.TalkBackUtils;
import com.lge.launcher3.util.Utilities;
import com.lge.launcher3.util.VibratorManager;
import com.lge.launcher3.util.ViewPosition;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsPagedView extends PagedView implements DragSource, DropTarget, DragScroller, DragController.DragListener, AllAppsView, View.OnTouchListener, View.OnClickListener, View.OnLongClickListener, AllAppsSortDialog.IAllAppsSortDialog, AllAppsSearchUtil.ISearchCallback {
    public static final boolean DEBUG_CLEANUP_ANI = false;
    public static final boolean DEBUG_DragAndDropPosition = false;
    public static final boolean DEBUG_InArrangeMode = false;
    public static final boolean DEBUG_Looping = false;
    public static final boolean DEBUG_highLight_position = false;
    private static final int HARDWARELAYER_NUM = 2;
    private static final int ITEM_PAGE = 0;
    private static final int ITEM_POS_COUNT = 3;
    private static final int ITEM_X = 1;
    private static final int ITEM_Y = 2;
    private static final int MESSAGE_MENU_RESET = 0;
    private static final int MESSAGE_OPEN_FOLDER = 6;
    private static final int MESSAGE_PAGEHANDLER_SETPAGE = 2;
    private static final int MESSAGE_REMOVE_LAYOUTDIALOG = 4;
    private static final int MESSAGE_SELECT_LAYOUT = 1;
    private static final int MESSAGE_SET_APPS = 3;
    private static final int MESSAGE_UPDATE_APPLIST = 5;
    private static final int PAGE_SNAP_ANIMATION_DURATION = 400;
    private static final int REARRANGE_DURATION = 190;
    private static final int SHRINK_EFFECT_DURATION = 300;
    private static final String TAG = "AllAppsPagedView";
    private static final int TIMER_RESET_PAGEMENU_DELAY = 200;
    private static float mCellLayout_middle_scale_xFactor;
    private static float mCellLayout_middle_scale_yFactor;
    private static int mCellLayout_scale_translationY;
    private static float mCellLayout_scale_xFactor;
    private static float mCellLayout_scale_yFactor;
    private boolean bAllowSwap;
    public ScreenEffectConst.FixedOverscrollState fixedOverscrollState;
    private boolean goingToShrink;
    private boolean goingToUnshrink;
    private Matrix inverseMatrix1;
    private Matrix inverseMatrix2;
    private final AllAppsApplicationUtil mAppUtil;
    private boolean mArrangeMode;
    private ArrayList<AppInfo> mCurrentApps;
    private float mDefault_Spacing;
    protected DragController mDragController;
    FolderIcon.FolderRingAnimator mDragFolderRingAnimator;
    private AllAppsItemInfo mDragItemFromFolder;
    private DragState mDragState;
    private FocusIndicatorView mFocusIndicatorView;
    private final Alarm mFolderCreationAlarm;
    private final Handler mHandler;
    protected IAllAppsHostListener mHostListener;
    private IconCache mIconCache;
    private final Point mIconLastTouchPos;
    private boolean mIsAllAppsLoaded;
    private boolean mIsFeatureEnabled;
    private boolean mIsPortrait;
    private boolean mIsScaleAni_Canceled;
    private boolean mIsShowSearchBar;
    boolean mIsSwivelLayout;
    private ShortcutInfo mItemFromFolder;
    private int mLastFolderAniIdx;
    protected Launcher mLauncher;
    private View.OnLayoutChangeListener mLayoutListener;
    private float mLayoutScale;
    private Handler mLongPressHandler;
    private int mMaxScrollForLoop;
    private AllAppsItemFactory mMenuItemFactory;
    private boolean mNeedReload;
    private int mNextTailPageScroll;
    private View.OnClickListener mOnClickListener;
    private View.OnLongClickListener mOnLongClickListener;
    private PageIndicatorExtension mPageIndicator;
    protected PersonalWorkSlidingTabStrip mPersonalWorkTabStrip;
    private int mPrevHeadPageScroll;
    private final Alarm mReArrangeAlarm;
    private final OnAlarmListener mReArrangeAlarmListener;
    protected boolean mRestoredArrangeMode;
    private Runnable mRunnable;
    private int mSaveInstanceStateItemIndex;
    private int mSearchSavedItemIndex;
    private AllAppsSearchUtil mSearchUtil;
    private AllAppsTextViewMngr mTextViewPool;
    private int mUnboundedScrollX;
    private boolean mWasInArrangeMode;
    private float mZoom_Spacing;
    private int mdragIndex;
    private int mdragPage;
    private int mswapIndex;
    public ScreenEffectConst.OverscrollState overscrollState;
    private ValueAnimator scaleAni;
    public ScreenEffectConst.ScrollDirection scrollDirection;
    public float scrollProgress;
    public ScreenEffectConst.WhichPageToDraw whichPageToDraw;

    private enum DragState {
        NONE,
        SCROLL_LEFT,
        SCROLL_RIGHT
    }

    private void cancelMakeFolder(boolean bAnimated) {
    }

    private BubbleTextView getTextViewFromFolder(ArrayList<ShortcutInfo> contents, AppInfo appInfo) {
        return null;
    }

    @Override // com.lge.launcher3.allapps.AllAppsView
    public void dumpState() {
    }

    @Override // com.lge.launcher3.PagedView
    protected void getEdgeVerticalPostion(int[] pos) {
    }

    @Override // com.android.launcher3.DragSource
    public float getIntrinsicIconScaleFactor() {
        return 0.0f;
    }

    public int indexOfHead() {
        return 0;
    }

    @Override // com.android.launcher3.DropTarget
    public boolean isDropEnabled() {
        return true;
    }

    @Override // com.android.launcher3.DropTarget
    public void onFlingToDelete(DropTarget.DragObject dragObject, PointF vec) {
    }

    @Override // com.android.launcher3.DragSource
    public void onFlingToDeleteCompleted() {
    }

    @Override // com.android.launcher3.DropTarget
    public void prepareAccessibilityDrop() {
    }

    @Override // com.lge.launcher3.PagedView, android.view.ViewGroup, android.view.ViewParent
    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        return false;
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsAppInfoDropTarget() {
        return false;
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsFlingToDelete() {
        return false;
    }

    @Override // com.lge.launcher3.allapps.AllAppsView
    public void surrender() {
    }

    @Override // com.lge.launcher3.allapps.AllAppsView
    public void zoom(float zoom, boolean animate) {
    }

    ArrayList<AllAppsPagedCellLayout> getAllAppsCellLayouts() {
        ArrayList<AllAppsPagedCellLayout> arrayList = new ArrayList<>();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            arrayList.add((AllAppsPagedCellLayout) getChildAt(i));
        }
        return arrayList;
    }

    public void removeItemsByList(final ArrayList<ShortcutInfo> removed, final FolderInfo ignoreFolder) {
        findFolderItemAndRemove(removed, ignoreFolder);
        removeItemsInAllApps(removed);
    }

    private void findFolderItemAndRemove(final ArrayList<ShortcutInfo> removed, final FolderInfo ignoreFolder) {
        Iterator<AllAppsPagedCellLayout> it = getAllAppsCellLayouts().iterator();
        while (it.hasNext()) {
            ShortcutAndWidgetContainer shortcutsAndWidgets = it.next().getShortcutsAndWidgets();
            HashMap map = new HashMap();
            for (int i = 0; i < shortcutsAndWidgets.getChildCount(); i++) {
                View childAt = shortcutsAndWidgets.getChildAt(i);
                map.put((ItemInfo) childAt.getTag(), childAt);
            }
            final HashMap map2 = new HashMap();
            filterShortcutInfos(map.keySet(), new LauncherModel.ItemInfoFilter() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.1
                @Override // com.android.launcher3.LauncherModel.ItemInfoFilter
                public boolean filterItem(ItemInfo parent, ItemInfo info, ComponentName cn) {
                    ArrayList arrayList;
                    if (!(parent instanceof FolderInfo) || parent == ignoreFolder || !removed.contains(info)) {
                        return false;
                    }
                    FolderInfo folderInfo = (FolderInfo) parent;
                    if (map2.containsKey(folderInfo)) {
                        arrayList = (ArrayList) map2.get(folderInfo);
                    } else {
                        ArrayList arrayList2 = new ArrayList();
                        map2.put(folderInfo, arrayList2);
                        arrayList = arrayList2;
                    }
                    arrayList.add((ShortcutInfo) info);
                    return true;
                }
            });
            for (FolderInfo folderInfo : map2.keySet()) {
                this.mMenuItemFactory.removeFolderItems(folderInfo, (ArrayList) map2.get(folderInfo));
            }
        }
    }

    static ArrayList<ItemInfo> filterShortcutInfos(Iterable<ItemInfo> infos, LauncherModel.ItemInfoFilter f) {
        HashSet hashSet = new HashSet();
        for (ItemInfo itemInfo : infos) {
            if (itemInfo instanceof ShortcutInfo) {
                ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
                if (f.filterItem(null, shortcutInfo, shortcutInfo.getTargetComponent())) {
                    hashSet.add(shortcutInfo);
                }
            } else if (itemInfo instanceof AllAppsItemInfo) {
                AllAppsItemInfo allAppsItemInfo = (AllAppsItemInfo) itemInfo;
                if (allAppsItemInfo.mFolderInfo != null) {
                    try {
                        for (ShortcutInfo shortcutInfo2 : allAppsItemInfo.mFolderInfo.getContents()) {
                            if (f.filterItem(allAppsItemInfo.mFolderInfo, shortcutInfo2, shortcutInfo2.getTargetComponent())) {
                                hashSet.add(shortcutInfo2);
                            }
                        }
                    } catch (ConcurrentModificationException e) {
                        Log.w(TAG, "Failed to filter folder items: " + e.getMessage());
                    }
                } else if (f.filterItem(null, allAppsItemInfo, allAppsItemInfo.componentName)) {
                    hashSet.add(allAppsItemInfo);
                }
            } else if (itemInfo instanceof FolderInfo) {
                FolderInfo folderInfo = (FolderInfo) itemInfo;
                try {
                    for (ShortcutInfo shortcutInfo3 : folderInfo.contents) {
                        if (f.filterItem(folderInfo, shortcutInfo3, shortcutInfo3.getTargetComponent())) {
                            hashSet.add(shortcutInfo3);
                        }
                    }
                } catch (ConcurrentModificationException e2) {
                    Log.w(TAG, "Failed to filter folder items: " + e2.getMessage());
                }
            }
        }
        return new ArrayList<>(hashSet);
    }

    public AllAppsPagedView(Context context) {
        this(context, null);
    }

    public AllAppsPagedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllAppsPagedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mRestoredArrangeMode = false;
        this.mArrangeMode = false;
        this.mIsFeatureEnabled = false;
        this.scaleAni = null;
        this.mdragIndex = -1;
        this.mdragPage = -1;
        this.mLastFolderAniIdx = -1;
        this.mswapIndex = -1;
        this.bAllowSwap = false;
        this.goingToShrink = false;
        this.goingToUnshrink = false;
        this.inverseMatrix1 = null;
        this.inverseMatrix2 = null;
        this.mDragItemFromFolder = null;
        this.mItemFromFolder = null;
        this.mFolderCreationAlarm = new Alarm();
        this.mDragFolderRingAnimator = null;
        this.mReArrangeAlarm = new Alarm();
        this.mLayoutScale = 1.0f;
        this.mIsAllAppsLoaded = false;
        this.mIsScaleAni_Canceled = false;
        this.mIsPortrait = true;
        this.mLayoutListener = null;
        this.mDragState = DragState.NONE;
        this.mIsShowSearchBar = false;
        this.mWasInArrangeMode = false;
        this.mAppUtil = new AllAppsApplicationUtil();
        this.mSaveInstanceStateItemIndex = -1;
        this.mSearchSavedItemIndex = -1;
        this.mIconLastTouchPos = new Point();
        this.mIsSwivelLayout = false;
        this.mReArrangeAlarmListener = new OnAlarmListener() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.14
            @Override // com.android.launcher3.OnAlarmListener
            public void onAlarm(Alarm alarm) {
                if (AllAppsPagedView.this.mScroller.isFinished()) {
                    AllAppsPagedView.this.rearrangingItems();
                }
            }
        };
        this.mLongPressHandler = new Handler();
        this.mHandler = new Handler() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.21
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        AllAppsPagedView.this.resetPageMenu();
                        break;
                    case 1:
                        AllAppsPagedView.this.setCellCount(msg.arg1, msg.arg2);
                        break;
                    case 2:
                        if (msg.arg1 >= 0) {
                            AllAppsPagedView.this.setCurrentPage(msg.arg1);
                        }
                        break;
                    case 3:
                        AllAppsPagedView allAppsPagedView = AllAppsPagedView.this;
                        allAppsPagedView.setApps(allAppsPagedView.mLauncher.mModel.getAllAppsList());
                        break;
                    case 4:
                        AllAppsPagedView.this.mLauncher.removeDialog(1);
                        break;
                    case 5:
                        AllAppsPagedView.this.updateAppList();
                        AllAppsPagedView.this.processReloadState();
                        break;
                    case 6:
                        AllAppsPagedView.this.openFolder(((Long) msg.obj).longValue(), true, false);
                        break;
                }
            }
        };
        this.mNeedReload = false;
        this.scrollDirection = ScreenEffectConst.ScrollDirection.NONE;
        this.overscrollState = ScreenEffectConst.OverscrollState.NONE;
        this.whichPageToDraw = ScreenEffectConst.WhichPageToDraw.NONE;
        this.fixedOverscrollState = ScreenEffectConst.FixedOverscrollState.NONE;
        this.scrollProgress = 0.0f;
        this.mPrevHeadPageScroll = 0;
        this.mNextTailPageScroll = 0;
        this.mMaxScrollForLoop = 0;
        this.mContext = context;
        this.mLauncher = (Launcher) context;
        setSoundEffectsEnabled(false);
        updateFeatureEnabled();
        if (context.getResources() == null) {
            return;
        }
        int integer = context.getResources().getInteger(R.integer.device_profile_allapps_default_numColumns);
        int integer2 = context.getResources().getInteger(R.integer.device_profile_allapps_default_numRows);
        if (this.mLauncher.getDeviceProfile().isTablet) {
            this.mCellCountX = integer;
            this.mCellCountY = integer2;
        } else {
            this.mCellCountX = LGInvariantDeviceProfile.getSharedPrefValue(context, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_COLUMNS, integer);
            this.mCellCountY = LGInvariantDeviceProfile.getSharedPrefValue(context, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_ROWS, integer2);
        }
        loadLayoutGap();
        setChildrenDrawingCacheEnabled(true);
        setChildOnClickListener(this);
        setChildOnLongClickListener(this);
        this.mMenuItemFactory = AllAppsItemFactory.initialize(this.mContext, this.mCellCountX, this.mCellCountY);
        this.mSearchUtil = new AllAppsSearchUtil(this);
        setHapticFeedbackEnabled(false);
        this.mIconCache = LauncherAppState.getInstance(context).getIconCache();
        setPageSpacing((int) this.mDefault_Spacing);
        this.mFadeInAdjacentScreens = false;
        this.mCurrentPage = 0;
    }

    @Override // com.lge.launcher3.PagedView
    protected void init() {
        super.init();
        this.mCenterPagesVertically = false;
        this.mDefault_Spacing = getResources().getDimension(R.dimen.all_apps_view_spacing_gap);
    }

    private void initValue() {
        Launcher launcher = this.mLauncher;
        if (launcher != null) {
            mCellLayout_scale_xFactor = launcher.getDeviceProfile().allAppsArrangeModeScaleFactor;
            mCellLayout_scale_yFactor = this.mLauncher.getDeviceProfile().allAppsArrangeModeScaleFactor;
            mCellLayout_middle_scale_xFactor = this.mLauncher.getDeviceProfile().allAppsArrangeModeScaleFactor;
            mCellLayout_middle_scale_yFactor = this.mLauncher.getDeviceProfile().allAppsArrangeModeScaleFactor;
            mCellLayout_scale_translationY = this.mLauncher.getDeviceProfile().allAppsArrangeModeTranslationY;
            this.mZoom_Spacing = getResources().getDimension(R.dimen.all_apps_view_zoom_spacing_gap);
            this.mDefault_Spacing = getResources().getDimension(R.dimen.all_apps_view_spacing_gap);
        }
    }

    private void setPageGrid() {
        int sharedPrefValue;
        int sharedPrefValue2;
        boolean value = LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue();
        this.mMenuItemFactory.setUseSwivelDB(value, "setPageGrid");
        int integer = this.mLauncher.getResources().getInteger(R.integer.device_profile_allapps_default_numColumns);
        int integer2 = this.mLauncher.getResources().getInteger(R.integer.device_profile_allapps_default_numRows);
        LGLog.d(TAG, "[ALLAPPS_DB]setPageGrid: isSwivel = " + value);
        if (value) {
            if (this.mLauncher.getDeviceProfile().isLandscape && this.mLauncher.getDeviceProfile().allowRotation) {
                sharedPrefValue = this.mLauncher.getResources().getInteger(R.integer.device_profile_allapps_swivel_home_numColumns_land);
                sharedPrefValue2 = this.mLauncher.getResources().getInteger(R.integer.device_profile_allapps_swivel_home_numRows_land);
            } else {
                sharedPrefValue = this.mLauncher.getResources().getInteger(R.integer.device_profile_allapps_swivel_home_numColumns_port);
                sharedPrefValue2 = this.mLauncher.getResources().getInteger(R.integer.device_profile_allapps_swivel_home_numRows_port);
            }
        } else {
            sharedPrefValue = LGInvariantDeviceProfile.getSharedPrefValue(this.mLauncher, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_COLUMNS, integer);
            sharedPrefValue2 = LGInvariantDeviceProfile.getSharedPrefValue(this.mLauncher, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_ROWS, integer2);
            int[] layoutNumFromPreference = AllAppsUtils.getLayoutNumFromPreference(getContext(), new int[2], true);
            if (layoutNumFromPreference[0] != sharedPrefValue || layoutNumFromPreference[1] != sharedPrefValue2) {
                AllAppsUtils.setNeedToSeriallization(true, "setPageGrid: (" + layoutNumFromPreference[0] + ", " + layoutNumFromPreference[1] + "),  (" + sharedPrefValue + ", " + sharedPrefValue2 + ")");
            }
        }
        setCellCount(sharedPrefValue, sharedPrefValue2);
    }

    public void updateFeatureEnabled() {
        if (LGHomeFeature.isEnableDefaultHome() || isInArrangeMode()) {
            this.mIsFeatureEnabled = false;
        } else if (LGFeatureConfig.FEATURE_OPERATOR.equals("VZW")) {
            this.mIsFeatureEnabled = HomeSettingsSharedPreferences.getVZWAppDrawerLoopEnabled(this.mContext);
        } else {
            this.mIsFeatureEnabled = LGHomeFeature.Config.FEATURE_APPDRAWER_LOOP_ENABLE.getValue();
        }
    }

    private boolean isEnableLoop() {
        return this.mIsFeatureEnabled && getChildCount() > 1;
    }

    public void resetForLoop() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).setId(i);
        }
        setCurrentPage(this.mCurrentPage);
        requestLayout();
        invalidate();
    }

    @Override // com.lge.launcher3.allapps.AllAppsSortDialog.IAllAppsSortDialog
    public void changeSortType(AllAppsSort.SortType sortType) {
        rearrangeBySortType(sortType);
        this.mMenuItemFactory.updatePositionChangedItems();
        setCurrentPage(0);
    }

    private void setChildOnClickListener(View.OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    private void setChildOnLongClickListener(View.OnLongClickListener listener) {
        this.mOnLongClickListener = listener;
    }

    @Override // com.lge.launcher3.allapps.AllAppsView
    public void setup(DragController dragController) {
        this.mDragController = dragController;
        int[] iArrInitLayout = this.mMenuItemFactory.initLayout();
        this.mIsPortrait = OrientationUtils.isPortrait(getContext());
        if (iArrInitLayout != null && iArrInitLayout.length >= 2) {
            this.mCellCountX = iArrInitLayout[0];
            this.mCellCountY = iArrInitLayout[1];
        }
        setCellCount(this.mCellCountX, this.mCellCountY);
        PageIndicatorExtension pageIndicatorExtension = (PageIndicatorExtension) ((ViewGroup) getParent()).findViewById(R.id.menu_page_indicator);
        this.mPageIndicator = pageIndicatorExtension;
        if (pageIndicatorExtension != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) pageIndicatorExtension.getLayoutParams();
            layoutParams.gravity = 81;
            layoutParams.width = -2;
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.device_profile_pageIndicator_height_allapps);
            this.mPageIndicator.setLayoutParams(layoutParams);
        }
    }

    public boolean isChangedLayout() {
        boolean z;
        boolean value = LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue();
        int childCount = getChildCount();
        boolean z2 = childCount == 0 || ((z = this.mIsSwivelLayout) && !value) || (!z && value);
        LGLog.d(TAG, "[ALLAPPS_DB]isChangedLayout: return " + z2 + ", count = " + childCount + ", mIsSwivelLayout = " + this.mIsSwivelLayout + ", isCarouselLayout = " + value);
        return z2;
    }

    void setCellCount(int xCount, int yCount) {
        if (!isChangedLayout() && xCount == this.mCellCountX && yCount == this.mCellCountY) {
            LGLog.i(TAG, "[ALLAPPS_DB] setCellCount skip.");
            return;
        }
        this.mCellCountX = xCount;
        this.mCellCountY = yCount;
        AllAppsUtils.saveLayoutNumToPreference(this.mContext, xCount, yCount);
        this.mMenuItemFactory.setCellCountXY(xCount, yCount, makeMenuItems(), isChangedLayout());
        loadLayoutGap();
        if (!LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            loadAllAppsList();
        }
        UninstallModeManager.getInstance(this.mContext).setUninstallTypeForAllBadgeViews(this);
        setCurrentPage(0);
    }

    private ArrayList<AllAppsItemInfo> makeMenuItems() {
        if (!this.mIsAllAppsLoaded) {
            LGLog.d(TAG, "[ALLAPPS_DB]makeMenuItems: return null because mIsAllAppsLoaded");
            return null;
        }
        if (isChangedLayout()) {
            LGLog.d(TAG, "[ALLAPPS_DB]makeMenuItems: return null. Changed Layout");
            return null;
        }
        ArrayList<AllAppsItemInfo> arrayList = new ArrayList<>();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(i);
            if (allAppsPagedCellLayout != null) {
                int childCount2 = allAppsPagedCellLayout.getShortcutsAndWidgets().getChildCount();
                for (int i2 = 0; i2 < childCount2; i2++) {
                    View childOnPageId = allAppsPagedCellLayout.getChildOnPageId(i2);
                    if (childOnPageId != null) {
                        arrayList.add((AllAppsItemInfo) childOnPageId.getTag());
                    }
                }
            }
        }
        return arrayList;
    }

    private void loadLayoutGap() {
        int dimensionPixelSize;
        int dimensionPixelSize2;
        Resources resources = getResources();
        int dimensionPixelSize3 = resources.getDimensionPixelSize(R.dimen.all_apps_pageview_padding_top);
        if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape && LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.dynamic_grid_page_indicator_height);
            dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.all_apps_pageview_padding_side_swivel_land);
        } else {
            dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.all_apps_pageview_padding_bottom) + resources.getDimensionPixelSize(R.dimen.dynamic_grid_page_indicator_height);
            dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.all_apps_pageview_padding_side);
        }
        setPaddingRelative(dimensionPixelSize2, dimensionPixelSize3, dimensionPixelSize2, dimensionPixelSize);
        PageIndicatorExtension pageIndicatorExtension = this.mPageIndicator;
        if (pageIndicatorExtension != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) pageIndicatorExtension.getLayoutParams();
            if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape && LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
                layoutParams.height = getResources().getDimensionPixelSize(R.dimen.device_profile_pageIndicator_height_allapps_swivel_land);
            } else {
                layoutParams.height = getResources().getDimensionPixelSize(R.dimen.device_profile_pageIndicator_height_allapps);
            }
            this.mPageIndicator.setLayoutParams(layoutParams);
        }
    }

    private void setupPage(AllAppsPagedCellLayout layout) {
        layout.setGridSize(this.mCellCountX, this.mCellCountY);
    }

    private boolean isChangedApps(ArrayList<AppInfo> list) {
        return this.mMenuItemFactory.syncAllAppsList(list) || !this.mIsAllAppsLoaded;
    }

    private void loadAllAppsList() {
        String str = TAG;
        LGLog.i(str, "[ALLAPPS_DB] loadAllAppsList : mIsSwivelLayout(" + this.mIsSwivelLayout + "->" + this.mMenuItemFactory.mUseSwivelDB + ")");
        this.mIsSwivelLayout = this.mMenuItemFactory.mUseSwivelDB;
        this.mLastScreenCenter = -1;
        resetAllAppsPageData();
        if (this.mAppUtil.isAllAppsEmpty()) {
            LGLog.i(str, "[ALLAPPS_DB] isAllAppsEmpty");
            return;
        }
        loadAllPages();
        View.OnLayoutChangeListener onLayoutChangeListener = this.mLayoutListener;
        if (onLayoutChangeListener != null) {
            removeOnLayoutChangeListener(onLayoutChangeListener);
        }
        View.OnLayoutChangeListener onLayoutChangeListener2 = new View.OnLayoutChangeListener() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.2
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (AllAppsPagedView.this.mSaveInstanceStateItemIndex >= 0) {
                    AllAppsPagedView allAppsPagedView = AllAppsPagedView.this;
                    allAppsPagedView.setCurrentPage(allAppsPagedView.mSaveInstanceStateItemIndex);
                    AllAppsPagedView.this.mSaveInstanceStateItemIndex = -1;
                } else {
                    AllAppsPagedView.this.setCurrentPage(0);
                }
                v.removeOnLayoutChangeListener(AllAppsPagedView.this.mLayoutListener);
                AllAppsPagedView.this.mLayoutListener = null;
                AllAppsPagedView.this.mSearchSavedItemIndex = -1;
            }
        };
        this.mLayoutListener = onLayoutChangeListener2;
        addOnLayoutChangeListener(onLayoutChangeListener2);
        requestLayout();
        setHardwareLayer(true);
        this.mIsAllAppsLoaded = true;
    }

    private void loadAllPages() {
        ArrayList<AllAppsItemInfo> allAppsItemInfoList = this.mMenuItemFactory.getAllAppsItemInfoList();
        if (this.mAppUtil.isAllAppsEmpty()) {
            return;
        }
        LGLog.i(TAG, "[ALLAPPS_DB] loadAllPages: start. mIsSwivelLayout = " + this.mIsSwivelLayout);
        for (AllAppsItemInfo allAppsItemInfo : allAppsItemInfoList) {
            if (allAppsItemInfo != null) {
                int i = (int) allAppsItemInfo.screenId;
                int i2 = (allAppsItemInfo.cellY * this.mCellCountX) + allAppsItemInfo.cellX;
                AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(i);
                if (allAppsPagedCellLayout != null) {
                    if (allAppsItemInfo.itemType == 0) {
                        allAppsPagedCellLayout.addViewToCellLayout(this.mTextViewPool.createMenuTextView(allAppsItemInfo, getIconBitmap(allAppsItemInfo)), -1, i2, new AllAppsPagedCellLayoutParam(allAppsItemInfo.cellX, allAppsItemInfo.cellY, 1, 1));
                    } else {
                        AllAppsFolderInfo allAppsFolderInfo = allAppsItemInfo.mFolderInfo;
                        FolderIcon folderIconCreateMenuFolderIcon = createMenuFolderIcon(allAppsPagedCellLayout, allAppsFolderInfo, allAppsFolderInfo.folderColor);
                        folderIconCreateMenuFolderIcon.setTag(allAppsItemInfo);
                        allAppsPagedCellLayout.addViewToCellLayout(folderIconCreateMenuFolderIcon, -1, i2, new AllAppsPagedCellLayoutParam(allAppsItemInfo.cellX, allAppsItemInfo.cellY, 1, 1));
                        allAppsItemInfo.itemView = folderIconCreateMenuFolderIcon;
                    }
                }
            }
        }
        restoreArrangeModeStateIfNeeded();
    }

    private void restoreArrangeModeStateIfNeeded() {
        if (this.mRestoredArrangeMode) {
            this.mCurrentPage = Math.max(0, Math.min(this.mSaveInstanceStateItemIndex, getPageCount() - 1));
            setCurrentPage(this.mCurrentPage);
            startArrangeMode(false);
            if (this.mSaveInstanceStateItemIndex == getChildCount() - 1) {
                setPageArrangeModeBg(this.mRestoredArrangeMode, 0);
                for (int i = 0; i < this.mSaveInstanceStateItemIndex; i++) {
                }
            }
            this.mRestoredArrangeMode = false;
        }
    }

    private void resetAllAppsPageData() {
        int maxPage = this.mMenuItemFactory.getMaxPage() + 1;
        unbindFolder();
        loadLayoutGap();
        removeAllAppsView();
        removeAllViews();
        for (int i = 0; i < maxPage; i++) {
            AllAppsPagedCellLayout allAppsPagedCellLayout = new AllAppsPagedCellLayout(getContext());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -1);
            setupPage(allAppsPagedCellLayout);
            allAppsPagedCellLayout.setId(i);
            addView(allAppsPagedCellLayout, layoutParams);
            allAppsPagedCellLayout.enableCenteredContent(false);
        }
        this.mCurrentPage = 0;
        setCurrentPage(this.mCurrentPage);
    }

    private FolderIcon createMenuFolderIcon(AllAppsPagedCellLayout layout, FolderInfo folderInfo) {
        return createMenuFolderIcon(layout, folderInfo, 0);
    }

    private FolderIcon createMenuFolderIcon(AllAppsPagedCellLayout layout, FolderInfo folderInfo, int fontcolor) {
        FolderIcon folderIconFromXml;
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            Launcher launcher = this.mLauncher;
            folderIconFromXml = FolderIcon.fromXml(R.layout.all_apps_folder_icon_swivel_home, launcher, layout, folderInfo, this.mIconCache, AllAppsFolder.fromXml(launcher), this.mFocusIndicatorView);
        } else {
            Launcher launcher2 = this.mLauncher;
            folderIconFromXml = FolderIcon.fromXml(R.layout.all_apps_folder_icon, launcher2, layout, folderInfo, this.mIconCache, AllAppsFolder.fromXml(launcher2), this.mFocusIndicatorView);
        }
        if (folderIconFromXml != null) {
            folderIconFromXml.setOnClickListener(this.mOnClickListener);
            folderIconFromXml.setOnLongClickListener(this.mOnLongClickListener);
            folderIconFromXml.invalidate();
        }
        return folderIconFromXml;
    }

    private AllAppsItemInfo addNewApplicationToLastPage(AppInfo appInfo, boolean immediately, boolean reverse) {
        AllAppsPagedCellLayout lastPositionForInsertNewProfileItem;
        AllAppsItemInfo allAppsItemInfoAddNewApplication;
        if (reverse) {
            insertAnotherPage(null, 0, 0, false);
            this.mMenuItemFactory.updatePositionChangedItems();
        }
        int[] iArr = new int[3];
        if (reverse) {
            lastPositionForInsertNewProfileItem = (AllAppsPagedCellLayout) getChildAt(0);
        } else {
            lastPositionForInsertNewProfileItem = AllAppsItemFactory.isManagedProfileItem(this.mContext, appInfo) ? getLastPositionForInsertNewProfileItem(iArr) : getLastPositionForInsertNewItem(iArr);
        }
        if (lastPositionForInsertNewProfileItem == null) {
            return null;
        }
        if (reverse) {
            allAppsItemInfoAddNewApplication = this.mMenuItemFactory.addNewApplication(0, 0, 0, appInfo, immediately);
        } else {
            allAppsItemInfoAddNewApplication = this.mMenuItemFactory.addNewApplication(iArr[0], iArr[1], iArr[2], appInfo, immediately);
        }
        if (allAppsItemInfoAddNewApplication == null) {
            return null;
        }
        BubbleTextView bubbleTextViewCreateMenuTextView = this.mTextViewPool.createMenuTextView(allAppsItemInfoAddNewApplication, getIconBitmap(allAppsItemInfoAddNewApplication));
        UninstallModeManager.getInstance(this.mContext).setUninstallTypeForBadgeViewAllApps(bubbleTextViewCreateMenuTextView);
        int i = (allAppsItemInfoAddNewApplication.cellY * this.mCellCountX) + allAppsItemInfoAddNewApplication.cellX;
        lastPositionForInsertNewProfileItem.addViewToCellLayout(bubbleTextViewCreateMenuTextView, i, i, new AllAppsPagedCellLayoutParam(allAppsItemInfoAddNewApplication.cellX, allAppsItemInfoAddNewApplication.cellY, 1, 1));
        if (getCurrentPage() != allAppsItemInfoAddNewApplication.screenId) {
            requestLayout();
            View.OnLayoutChangeListener onLayoutChangeListener = this.mLayoutListener;
            if (onLayoutChangeListener != null) {
                removeOnLayoutChangeListener(onLayoutChangeListener);
            }
            View.OnLayoutChangeListener onLayoutChangeListener2 = new View.OnLayoutChangeListener() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.3
                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(AllAppsPagedView.this.mLayoutListener);
                    AllAppsPagedView.this.mLayoutListener = null;
                }
            };
            this.mLayoutListener = onLayoutChangeListener2;
            addOnLayoutChangeListener(onLayoutChangeListener2);
        } else {
            requestLayout();
        }
        return allAppsItemInfoAddNewApplication;
    }

    private AllAppsItemInfo addApplication(AppInfo appInfo, int page, int index) {
        if (((AllAppsPagedCellLayout) getChildAt(page)) == null) {
            return null;
        }
        AllAppsItemInfo allAppsItemInfoAddNewApplication = this.mMenuItemFactory.addNewApplication(page, index % this.mCellCountX, index / this.mCellCountX, appInfo, true);
        BubbleTextView bubbleTextViewCreateMenuTextView = this.mTextViewPool.createMenuTextView(allAppsItemInfoAddNewApplication, getIconBitmap(allAppsItemInfoAddNewApplication));
        UninstallModeManager.getInstance(this.mContext).setUninstallTypeForBadgeViewAllApps(bubbleTextViewCreateMenuTextView);
        insertAnotherPage(bubbleTextViewCreateMenuTextView, page, index, false);
        return allAppsItemInfoAddNewApplication;
    }

    private AllAppsPagedCellLayout getLastPositionForInsertNewItem(int[] newPosition) {
        int i;
        int i2;
        int i3;
        AllAppsPagedCellLayout allAppsPagedCellLayout = new AllAppsPagedCellLayout(getContext());
        int childCount = getChildCount();
        int managedProfileStartPage = this.mMenuItemFactory.getManagedProfileStartPage();
        String str = TAG;
        LGLog.d(str, "managedProfilePage: " + managedProfileStartPage + ", childCount: " + childCount);
        if (childCount > 0) {
            int i4 = managedProfileStartPage == -1 ? childCount - 1 : managedProfileStartPage - 1;
            i = i4;
            allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(i4);
        } else {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -1);
            setupPage(allAppsPagedCellLayout);
            addView(allAppsPagedCellLayout, layoutParams);
            allAppsPagedCellLayout.enableCenteredContent(false);
            i = 0;
        }
        int childCount2 = allAppsPagedCellLayout.getShortcutsAndWidgets().getChildCount();
        LGLog.d(str, "isArrangeMode: " + this.mArrangeMode);
        if (this.mArrangeMode) {
            if (childCount2 == 0) {
                i = childCount - 2;
                allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(i);
                childCount2 = allAppsPagedCellLayout.getShortcutsAndWidgets().getChildCount();
                if (childCount2 >= this.mCellCountX * this.mCellCountY) {
                    i = childCount - 1;
                    allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(i);
                    childCount2 = allAppsPagedCellLayout.getShortcutsAndWidgets().getChildCount();
                }
            } else if (childCount > 0) {
                i = childCount - 1;
            }
        }
        if (childCount2 >= this.mCellCountX * this.mCellCountY) {
            allAppsPagedCellLayout = new AllAppsPagedCellLayout(getContext());
            ViewGroup.LayoutParams layoutParams2 = new ViewGroup.LayoutParams(-1, -1);
            setupPage(allAppsPagedCellLayout);
            i++;
            LGLog.d(str, "pageForInsert: " + i);
            if (managedProfileStartPage != -1) {
                addView(allAppsPagedCellLayout, i, layoutParams2);
            } else {
                addView(allAppsPagedCellLayout, layoutParams2);
            }
            allAppsPagedCellLayout.enableCenteredContent(false);
            if (this.mArrangeMode) {
                allAppsPagedCellLayout.setShrinkEffect(true);
                this.mFirstLayout = true;
                allAppsPagedCellLayout.setScaleX(mCellLayout_scale_xFactor);
                allAppsPagedCellLayout.setScaleY(mCellLayout_scale_yFactor);
                allAppsPagedCellLayout.setTranslationY(mCellLayout_scale_translationY);
                allAppsPagedCellLayout.setBGAlpha(255);
                allAppsPagedCellLayout.invalidate();
            }
            this.mMenuItemFactory.updateManagedProfileItemScreenId();
            requestLayout();
            i2 = 0;
            i3 = 0;
        } else {
            i2 = childCount2 % this.mCellCountX;
            i3 = childCount2 / this.mCellCountX;
        }
        newPosition[0] = i;
        newPosition[1] = i2;
        newPosition[2] = i3;
        return allAppsPagedCellLayout;
    }

    public int getManagedProfileStartPage() {
        return this.mMenuItemFactory.getManagedProfileStartPage();
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0069  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private com.lge.launcher3.allapps.AllAppsPagedCellLayout getLastPositionForInsertNewProfileItem(int[] r11) {
        /*
            r10 = this;
            com.lge.launcher3.allapps.AllAppsPagedCellLayout r0 = new com.lge.launcher3.allapps.AllAppsPagedCellLayout
            android.content.Context r1 = r10.getContext()
            r0.<init>(r1)
            int r1 = r10.getChildCount()
            r2 = -1
            r3 = 0
            if (r1 <= 0) goto L1d
            int r0 = r1 + (-1)
            android.view.View r4 = r10.getChildAt(r0)
            com.lge.launcher3.allapps.AllAppsPagedCellLayout r4 = (com.lge.launcher3.allapps.AllAppsPagedCellLayout) r4
            r9 = r4
            r4 = r0
            r0 = r9
            goto L2c
        L1d:
            android.view.ViewGroup$LayoutParams r4 = new android.view.ViewGroup$LayoutParams
            r4.<init>(r2, r2)
            r10.setupPage(r0)
            r10.addView(r0, r4)
            r0.enableCenteredContent(r3)
            r4 = r3
        L2c:
            com.android.launcher3.ShortcutAndWidgetContainer r5 = r0.getShortcutsAndWidgets()
            int r5 = r5.getChildCount()
            boolean r6 = r10.mArrangeMode
            r7 = 1
            if (r6 == 0) goto L67
            if (r5 != 0) goto L64
            int r4 = r1 + (-2)
            android.view.View r0 = r10.getChildAt(r4)
            com.lge.launcher3.allapps.AllAppsPagedCellLayout r0 = (com.lge.launcher3.allapps.AllAppsPagedCellLayout) r0
            com.android.launcher3.ShortcutAndWidgetContainer r5 = r0.getShortcutsAndWidgets()
            int r5 = r5.getChildCount()
            int r6 = r10.mCellCountX
            int r8 = r10.mCellCountY
            int r6 = r6 * r8
            if (r5 >= r6) goto L53
            goto L6b
        L53:
            int r4 = r1 + (-1)
            android.view.View r0 = r10.getChildAt(r4)
            com.lge.launcher3.allapps.AllAppsPagedCellLayout r0 = (com.lge.launcher3.allapps.AllAppsPagedCellLayout) r0
            com.android.launcher3.ShortcutAndWidgetContainer r1 = r0.getShortcutsAndWidgets()
            int r5 = r1.getChildCount()
            goto L6b
        L64:
            if (r1 <= 0) goto L6b
            goto L69
        L67:
            if (r1 <= 0) goto L6b
        L69:
            int r4 = r1 + (-1)
        L6b:
            com.lge.launcher3.allapps.AllAppsItemFactory r1 = r10.mMenuItemFactory
            int r1 = r1.getManagedProfileStartPage()
            int r6 = r10.mCellCountX
            int r8 = r10.mCellCountY
            int r6 = r6 * r8
            if (r5 >= r6) goto L83
            if (r1 != r2) goto L7b
            goto L83
        L7b:
            int r1 = r10.mCellCountX
            int r1 = r5 % r1
            int r2 = r10.mCellCountX
            int r5 = r5 / r2
            goto Lc5
        L83:
            com.lge.launcher3.allapps.AllAppsPagedCellLayout r0 = new com.lge.launcher3.allapps.AllAppsPagedCellLayout
            android.content.Context r1 = r10.getContext()
            r0.<init>(r1)
            android.view.ViewGroup$LayoutParams r1 = new android.view.ViewGroup$LayoutParams
            r1.<init>(r2, r2)
            r10.setupPage(r0)
            r10.addView(r0, r1)
            r0.enableCenteredContent(r3)
            boolean r1 = r10.mArrangeMode
            if (r1 == 0) goto Lbb
            r0.setShrinkEffect(r7)
            r10.mFirstLayout = r7
            float r1 = com.lge.launcher3.allapps.AllAppsPagedView.mCellLayout_scale_xFactor
            r0.setScaleX(r1)
            float r1 = com.lge.launcher3.allapps.AllAppsPagedView.mCellLayout_scale_yFactor
            r0.setScaleY(r1)
            int r1 = com.lge.launcher3.allapps.AllAppsPagedView.mCellLayout_scale_translationY
            float r1 = (float) r1
            r0.setTranslationY(r1)
            r1 = 255(0xff, float:3.57E-43)
            r0.setBGAlpha(r1)
            r0.invalidate()
        Lbb:
            int r4 = r4 + 1
            r10.requestLayout()
            r10.invalidate()
            r1 = r3
            r5 = r1
        Lc5:
            r11[r3] = r4
            r11[r7] = r1
            r1 = 2
            r11[r1] = r5
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.allapps.AllAppsPagedView.getLastPositionForInsertNewProfileItem(int[]):com.lge.launcher3.allapps.AllAppsPagedCellLayout");
    }

    private AllAppsItemInfo makeApplicationToFolder(BubbleTextView icon) {
        AllAppsItemInfo allAppsItemInfo = (AllAppsItemInfo) icon.getTag();
        if (allAppsItemInfo == null) {
            return null;
        }
        AllAppsPagedCellLayoutParam allAppsPagedCellLayoutParam = (AllAppsPagedCellLayoutParam) icon.getLayoutParams();
        ((AllAppsPagedCellLayout) icon.getParent().getParent()).removeViewOnPageId((allAppsPagedCellLayoutParam.cellY * this.mCellCountX) + allAppsPagedCellLayoutParam.cellX);
        AllAppsItemInfo allAppsItemInfoAddNewFolder = addNewFolder((int) allAppsItemInfo.screenId, allAppsPagedCellLayoutParam.cellX, allAppsPagedCellLayoutParam.cellY);
        if (allAppsItemInfoAddNewFolder == null) {
            return null;
        }
        ((FolderIcon) allAppsItemInfoAddNewFolder.itemView).getFolderInfo().add(new ShortcutInfo((AllAppsItemInfo) icon.getTag()));
        this.mMenuItemFactory.removeItemInfo(allAppsItemInfo);
        AccessibilityManager accessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
        if (accessibilityManager.isEnabled()) {
            AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(32);
            accessibilityEventObtain.getText().add(this.mContext.getText(R.string.sp_talkback_folder_created));
            accessibilityEventObtain.setSource(this, 0);
            accessibilityManager.sendAccessibilityEvent(accessibilityEventObtain);
        }
        return allAppsItemInfoAddNewFolder;
    }

    protected AllAppsItemInfo makeFolderToApplication(AllAppsItemInfo folderInfo) {
        AllAppsFolderInfo allAppsFolderInfo = folderInfo.mFolderInfo;
        AllAppsItemInfo allAppsItemInfoAddNewApplication = null;
        if (allAppsFolderInfo == null) {
            LGLog.e(TAG, "makeFolderToApplication() : Invalid icon info.");
            return null;
        }
        if (folderInfo == null) {
            return null;
        }
        if (allAppsFolderInfo.getContents().isEmpty()) {
            LGLog.d(TAG, "Folder contents is empty.");
            removeFolder(folderInfo);
        } else {
            ShortcutInfo shortcutInfo = allAppsFolderInfo.getContents().get(0);
            AppInfo appInfoFindAppByComponent = this.mAppUtil.findAppByComponent(shortcutInfo.intent.getComponent(), shortcutInfo.user);
            if (appInfoFindAppByComponent != null) {
                ShortcutAndWidgetContainer shortcutAndWidgetContainer = (ShortcutAndWidgetContainer) folderInfo.itemView.getParent();
                AllAppsPagedCellLayout allAppsPagedCellLayout = shortcutAndWidgetContainer != null ? (AllAppsPagedCellLayout) shortcutAndWidgetContainer.getParent() : null;
                if (allAppsPagedCellLayout == null) {
                    return null;
                }
                allAppsPagedCellLayout.removeViewOnPageId((folderInfo.cellY * this.mCellCountX) + folderInfo.cellX);
                allAppsItemInfoAddNewApplication = addNewApplication((int) folderInfo.screenId, folderInfo.cellX, folderInfo.cellY, appInfoFindAppByComponent);
                this.mMenuItemFactory.removeFolder(folderInfo);
                this.mMenuItemFactory.updatePositionChangedItems();
            } else {
                removeFolder(folderInfo);
            }
        }
        this.mdragIndex = -1;
        return allAppsItemInfoAddNewApplication;
    }

    private AllAppsItemInfo addNewFolder(int pageID, int cellX, int cellY) {
        int i;
        AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(pageID);
        if (allAppsPagedCellLayout == null || (i = (this.mCellCountX * cellY) + cellX) > allAppsPagedCellLayout.getShortcutsAndWidgets().getChildCount()) {
            return null;
        }
        AllAppsItemInfo allAppsItemInfoAddNewFolder = this.mMenuItemFactory.addNewFolder(this.mContext.getText(R.string.folder_name).toString(), pageID, cellX, cellY);
        FolderIcon folderIconCreateMenuFolderIcon = createMenuFolderIcon(allAppsPagedCellLayout, allAppsItemInfoAddNewFolder.mFolderInfo);
        allAppsPagedCellLayout.addViewToCellLayout(folderIconCreateMenuFolderIcon, i, i, new AllAppsPagedCellLayoutParam(cellX, cellY, 1, 1));
        folderIconCreateMenuFolderIcon.setTag(allAppsItemInfoAddNewFolder);
        allAppsItemInfoAddNewFolder.itemView = folderIconCreateMenuFolderIcon;
        allAppsPagedCellLayout.getShortcutsAndWidgets().measureChild(folderIconCreateMenuFolderIcon);
        requestLayout();
        return allAppsItemInfoAddNewFolder;
    }

    private AllAppsItemInfo addNewApplication(int pageID, int cellX, int cellY, AppInfo appInfo) {
        AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(pageID);
        if (allAppsPagedCellLayout == null) {
            return null;
        }
        int i = (this.mCellCountX * cellY) + cellX;
        AllAppsItemInfo allAppsItemInfo = new AllAppsItemInfo(appInfo);
        allAppsItemInfo.cellX = cellX;
        allAppsItemInfo.cellY = cellY;
        allAppsItemInfo.screenId = pageID;
        BubbleTextView bubbleTextViewCreateMenuTextView = this.mTextViewPool.createMenuTextView(allAppsItemInfo, getIconBitmap(allAppsItemInfo));
        UninstallModeManager.getInstance(this.mContext).setUninstallTypeForBadgeViewAllApps(bubbleTextViewCreateMenuTextView);
        allAppsPagedCellLayout.addViewToCellLayout(bubbleTextViewCreateMenuTextView, i, i, new AllAppsPagedCellLayoutParam(cellX, cellY, 1, 1));
        bubbleTextViewCreateMenuTextView.setTag(allAppsItemInfo);
        this.mMenuItemFactory.addNewItemInfo(allAppsItemInfo);
        requestLayout();
        return allAppsItemInfo;
    }

    public AllAppsItemInfo addViewNewApplication(int pageID, int cellX, int cellY, ShortcutInfo shortcutInfo) {
        AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(pageID);
        if (allAppsPagedCellLayout == null) {
            return null;
        }
        AppInfo appInfoFindAppByComponent = this.mAppUtil.findAppByComponent(shortcutInfo.intent.getComponent(), shortcutInfo.user);
        if (appInfoFindAppByComponent == null) {
            LGLog.d(TAG, "fail addViewNewApplication because appInfo can't found by component");
            return null;
        }
        int i = (this.mCellCountX * cellY) + cellX;
        AllAppsItemInfo allAppsItemInfo = new AllAppsItemInfo(appInfoFindAppByComponent);
        allAppsItemInfo.cellX = cellX;
        allAppsItemInfo.cellY = cellY;
        allAppsItemInfo.screenId = pageID;
        BubbleTextView bubbleTextViewCreateMenuTextView = this.mTextViewPool.createMenuTextView(allAppsItemInfo, getIconBitmap(allAppsItemInfo));
        UninstallModeManager.getInstance(this.mContext).setUninstallTypeForBadgeViewAllApps(bubbleTextViewCreateMenuTextView);
        allAppsPagedCellLayout.addViewToCellLayout(bubbleTextViewCreateMenuTextView, i, i, new AllAppsPagedCellLayoutParam(cellX, cellY, 1, 1));
        this.mMenuItemFactory.addNewItemInfo(allAppsItemInfo);
        requestLayout();
        return allAppsItemInfo;
    }

    void removeFolder(AllAppsItemInfo folderItemInfo) {
        if (this.mMenuItemFactory.hasFolder(folderItemInfo)) {
            AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt((int) folderItemInfo.screenId);
            if (allAppsPagedCellLayout != null) {
                allAppsPagedCellLayout.removeNarrangePage((folderItemInfo.cellY * this.mCellCountX) + folderItemInfo.cellX, false);
                allAppsPagedCellLayout.invalidate();
            }
            this.mMenuItemFactory.removeFolder(folderItemInfo);
            this.mMenuItemFactory.updatePositionChangedItems();
            if (allAppsPagedCellLayout == null || allAppsPagedCellLayout.getChildOnPageAt(0) != null) {
                return;
            }
            removeVacantPage();
        }
    }

    private void getMapPointWithScale(int[] point, float scale) {
        float[] fArr = {point[0], point[1]};
        AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(getCurrentPage());
        if (allAppsPagedCellLayout == null) {
            return;
        }
        float scaleX = allAppsPagedCellLayout.getScaleX();
        allAppsPagedCellLayout.setScaleX(scale);
        allAppsPagedCellLayout.setScaleY(scale);
        allAppsPagedCellLayout.setTranslationY(mCellLayout_scale_translationY);
        allAppsPagedCellLayout.getShortcutsAndWidgets().getMatrix().mapPoints(fArr);
        allAppsPagedCellLayout.getMatrix().mapPoints(fArr);
        allAppsPagedCellLayout.setScaleX(scaleX);
        allAppsPagedCellLayout.setScaleX(scaleX);
        point[0] = (int) fArr[0];
        point[1] = (int) fArr[1];
    }

    private void getInverseMappoint(float[] point) {
        AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(getCurrentPage());
        if (allAppsPagedCellLayout == null) {
            return;
        }
        if (this.inverseMatrix1 == null) {
            Matrix matrix = allAppsPagedCellLayout.getShortcutsAndWidgets().getMatrix();
            Matrix matrix2 = new Matrix();
            this.inverseMatrix1 = matrix2;
            matrix.invert(matrix2);
        }
        if (this.inverseMatrix2 == null) {
            Matrix matrix3 = allAppsPagedCellLayout.getMatrix();
            Matrix matrix4 = new Matrix();
            this.inverseMatrix2 = matrix4;
            matrix3.invert(matrix4);
        }
        this.inverseMatrix1.mapPoints(point);
        this.inverseMatrix2.mapPoints(point);
    }

    @Override // com.lge.launcher3.PagedView
    protected void determineScrollingStart(MotionEvent ev) {
        if (getChildCount() > 1) {
            super.determineScrollingStart(ev);
        }
    }

    private void talkbackReadCurrentPosition(boolean readTotal) {
        if (TalkBackUtils.isEnabled(getContext())) {
            StringBuilder sb = new StringBuilder();
            if (readTotal) {
                sb.append(String.format(this.mContext.getString(R.string.default_scroll_format), Integer.valueOf((this.mNextPage != -1 ? this.mNextPage : getCurrentPage()) + 1), Integer.valueOf(getChildCount())));
            }
            sb.append(getResources().getString(R.string.talkback_grid_locate_folder, Integer.valueOf((this.mdragIndex % this.mCellCountX) + 1), Integer.valueOf((this.mdragIndex / this.mCellCountX) + 1)));
            announceForAccessibility(sb.toString());
        }
    }

    private FolderInfo addFolder(AllAppsItemInfo itemInfo) {
        AllAppsFolderInfo allAppsFolderInfo = itemInfo.mFolderInfo;
        FolderInfo folderInfo = new FolderInfo();
        folderInfo.setTitle(allAppsFolderInfo.title.toString());
        folderInfo.changeFolderColor(allAppsFolderInfo.folderColor);
        for (ShortcutInfo shortcutInfo : allAppsFolderInfo.getContents()) {
            if (shortcutInfo != null) {
                folderInfo.add(shortcutInfo);
            }
        }
        folderInfo.folderColor = allAppsFolderInfo.folderColor;
        folderInfo.container = -1L;
        Iterator<ShortcutInfo> it = folderInfo.getContents().iterator();
        while (it.hasNext()) {
            it.next().container = -1L;
        }
        return folderInfo;
    }

    @Override // com.lge.launcher3.PagedView
    protected void onPageEndTransition() {
        super.onPageEndTransition();
        if (this.goingToShrink) {
            shrink();
        } else if (this.goingToUnshrink) {
            unshrink();
        }
    }

    private void shrink() {
        shrink(true);
    }

    private void shrink(boolean animated) {
        ValueAnimator valueAnimator = this.scaleAni;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.goingToShrink = false;
        this.mIsScaleAni_Canceled = true;
        AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(this.mCurrentPage);
        if (allAppsPagedCellLayout == null) {
            return;
        }
        allAppsPagedCellLayout.setShrinkEffect(true);
        allAppsPagedCellLayout.invalidate();
        View childAt = getChildAt(this.mCurrentPage);
        if (childAt == null) {
            return;
        }
        addVacantPage();
        requestLayout();
        if (isEnableLoop()) {
            int id = allAppsPagedCellLayout.getId();
            int childCount = getChildCount();
            if (id == 0 && getChildCount() > 2) {
                int i = childCount - 1;
                View view = null;
                for (int i2 = i; i2 >= 0; i2--) {
                    View pageAt = getPageAt(i2);
                    if (pageAt != null) {
                        if (i2 == i) {
                            view = pageAt;
                        } else {
                            pageAt.setId(i2 + 1);
                        }
                    }
                }
                if (view != null) {
                    view.setId(0);
                }
            } else if (id == getChildCount() - 1) {
                View view2 = null;
                for (int i3 = 0; i3 < childCount; i3++) {
                    View pageAt2 = getPageAt(i3);
                    if (pageAt2 != null) {
                        if (i3 == 0) {
                            view2 = pageAt2;
                        } else {
                            pageAt2.setId(i3 - 1);
                        }
                    }
                }
                if (view2 != null) {
                    view2.setId(childCount - 1);
                }
            }
        }
        if (animated) {
            final int width = childAt.getWidth();
            final float f = (width * (1.0f - mCellLayout_scale_xFactor)) - this.mZoom_Spacing;
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(300L);
            this.scaleAni = duration;
            duration.setInterpolator(new OvershootInterpolator(1.5f));
            this.scaleAni.addListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.4
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    AllAppsPagedView.this.mIsScaleAni_Canceled = false;
                    AllAppsPagedView.this.setHardwareLayer(false);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    AllAppsPagedView allAppsPagedView = AllAppsPagedView.this;
                    allAppsPagedView.setPageSpacing((int) allAppsPagedView.mZoom_Spacing);
                    if (!AllAppsPagedView.this.mIsScaleAni_Canceled) {
                        LGLog.d("[PageMenu]", "SHRINK EFFECT END  setHardwareLayerNear true");
                        AllAppsPagedView.this.mArrangeMode = true;
                        AllAppsPagedView.this.updateFeatureEnabled();
                    } else {
                        AllAppsPagedView.this.mIsScaleAni_Canceled = false;
                    }
                    AllAppsPagedView.this.setScale(true);
                    AllAppsPagedView.this.scaleAni = null;
                    AllAppsPagedView.this.setHardwareLayer(true);
                    AllAppsPagedView.this.setChildFocus();
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    AllAppsPagedView.this.mIsScaleAni_Canceled = true;
                    AllAppsPagedView.this.setHardwareLayer(true);
                }
            });
            this.scaleAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.5
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    Object animatedValue = animation.getAnimatedValue();
                    if (animatedValue == null) {
                        return;
                    }
                    float fFloatValue = ((Float) animatedValue).floatValue();
                    float f2 = 1.0f - fFloatValue;
                    int childCount2 = (AllAppsPagedView.this.mCurrentPage == 0 ? AllAppsPagedView.this.getChildCount() : AllAppsPagedView.this.mCurrentPage) - 1;
                    int i4 = AllAppsPagedView.this.mCurrentPage == AllAppsPagedView.this.getChildCount() - 1 ? 0 : AllAppsPagedView.this.mCurrentPage + 1;
                    AllAppsPagedView allAppsPagedView = AllAppsPagedView.this;
                    AllAppsPagedCellLayout allAppsPagedCellLayout2 = (AllAppsPagedCellLayout) allAppsPagedView.getChildAt(allAppsPagedView.mCurrentPage);
                    float x = 0.0f;
                    if (allAppsPagedCellLayout2 != null) {
                        allAppsPagedCellLayout2.setShrinkEffect(true);
                        x = allAppsPagedCellLayout2.getX();
                        float f3 = f2 * 1.0f;
                        allAppsPagedCellLayout2.setScaleX((AllAppsPagedView.mCellLayout_middle_scale_xFactor * fFloatValue) + f3);
                        allAppsPagedCellLayout2.setScaleY(f3 + (AllAppsPagedView.mCellLayout_middle_scale_yFactor * fFloatValue));
                        allAppsPagedCellLayout2.setTranslationY(AllAppsPagedView.mCellLayout_scale_translationY);
                        allAppsPagedCellLayout2.setBGAlpha((int) (fFloatValue * 255.0f));
                        allAppsPagedCellLayout2.invalidate();
                    }
                    AllAppsPagedCellLayout allAppsPagedCellLayout3 = (AllAppsPagedCellLayout) AllAppsPagedView.this.getChildAt(childCount2);
                    if (allAppsPagedCellLayout3 != null && (AllAppsPagedView.this.mIsRtl || AllAppsPagedView.this.mCurrentPage != 0)) {
                        allAppsPagedCellLayout3.setX((x - width) + (f * fFloatValue));
                        float f4 = f2 * 1.0f;
                        allAppsPagedCellLayout3.setScaleX((AllAppsPagedView.mCellLayout_scale_xFactor * fFloatValue) + f4);
                        allAppsPagedCellLayout3.setScaleY(f4 + (AllAppsPagedView.mCellLayout_scale_yFactor * fFloatValue));
                        allAppsPagedCellLayout3.setTranslationY(AllAppsPagedView.mCellLayout_scale_translationY);
                        allAppsPagedCellLayout3.setBGAlpha((int) (fFloatValue * 255.0f));
                        allAppsPagedCellLayout3.invalidate();
                    }
                    AllAppsPagedCellLayout allAppsPagedCellLayout4 = (AllAppsPagedCellLayout) AllAppsPagedView.this.getChildAt(i4);
                    if (allAppsPagedCellLayout4 != null && (!AllAppsPagedView.this.mIsRtl || AllAppsPagedView.this.mCurrentPage != 0)) {
                        allAppsPagedCellLayout4.setX((x + width) - (f * fFloatValue));
                        float f5 = f2 * 1.0f;
                        allAppsPagedCellLayout4.setScaleX((AllAppsPagedView.mCellLayout_scale_xFactor * fFloatValue) + f5);
                        allAppsPagedCellLayout4.setScaleY(f5 + (AllAppsPagedView.mCellLayout_scale_yFactor * fFloatValue));
                        allAppsPagedCellLayout4.setTranslationY(AllAppsPagedView.mCellLayout_scale_translationY);
                        allAppsPagedCellLayout4.setBGAlpha((int) (fFloatValue * 255.0f));
                        allAppsPagedCellLayout4.invalidate();
                    }
                    AllAppsPagedView.this.invalidate();
                }
            });
            this.scaleAni.start();
            return;
        }
        allAppsPagedCellLayout.setScaleX(mCellLayout_middle_scale_xFactor);
        allAppsPagedCellLayout.setScaleY(mCellLayout_middle_scale_yFactor);
        allAppsPagedCellLayout.setTranslationY(mCellLayout_scale_translationY);
        allAppsPagedCellLayout.setBGAlpha(255);
        setPageSpacing((int) this.mZoom_Spacing);
        if (!this.mIsScaleAni_Canceled) {
            this.mArrangeMode = true;
            updateFeatureEnabled();
        } else {
            this.mIsScaleAni_Canceled = false;
        }
        setScale(true);
        this.scaleAni = null;
        setHardwareLayer(true);
        setChildFocus();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setScale(boolean bShrink) {
        int pageCount = getPageCount();
        View childAt = getChildAt(this.mCurrentPage);
        int id = childAt != null ? childAt.getId() : -1;
        for (int i = 0; i < pageCount; i++) {
            AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getPageAt(i);
            if (allAppsPagedCellLayout != null) {
                if (!bShrink) {
                    allAppsPagedCellLayout.setScaleX(1.0f);
                    allAppsPagedCellLayout.setScaleY(1.0f);
                    allAppsPagedCellLayout.setTranslationY(0.0f);
                    allAppsPagedCellLayout.setShrinkEffect(false);
                } else if (i != id) {
                    allAppsPagedCellLayout.setScaleX(mCellLayout_scale_xFactor);
                    allAppsPagedCellLayout.setScaleY(mCellLayout_scale_yFactor);
                    allAppsPagedCellLayout.setTranslationY(mCellLayout_scale_translationY);
                    allAppsPagedCellLayout.setBGAlpha(255);
                }
                allAppsPagedCellLayout.setTranslationX(0.0f);
                allAppsPagedCellLayout.invalidate();
            }
        }
        if (bShrink) {
            this.mLayoutScale = mCellLayout_scale_xFactor;
        } else {
            this.mLayoutScale = 1.0f;
        }
        updateCurrentPageScroll();
        requestLayout();
    }

    public void unshrink() {
        unshrink(true);
    }

    public void unshrink(boolean animated) {
        ValueAnimator valueAnimator = this.scaleAni;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.mIsScaleAni_Canceled = true;
        this.goingToUnshrink = false;
        removeVacantPage();
        requestLayout();
        View childAt = getChildAt(this.mCurrentPage);
        if (childAt == null) {
            animated = false;
        }
        if (animated) {
            final int id = childAt.getId();
            final int iMax = Math.max(0, id - 1);
            final int iMin = Math.min(getChildCount() - 1, id + 1);
            final AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getPageAt(this.mCurrentPage);
            final AllAppsPagedCellLayout allAppsPagedCellLayout2 = (AllAppsPagedCellLayout) getPageAt(iMax);
            final AllAppsPagedCellLayout allAppsPagedCellLayout3 = (AllAppsPagedCellLayout) getPageAt(iMin);
            final float measuredWidth = (getMeasuredWidth() * (1.0f - mCellLayout_scale_xFactor)) - this.mZoom_Spacing;
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(300L);
            this.scaleAni = duration;
            duration.setInterpolator(new OvershootInterpolator(1.0f));
            this.scaleAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.6
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    Object animatedValue = animation.getAnimatedValue();
                    if (animatedValue == null) {
                        return;
                    }
                    float fFloatValue = ((Float) animatedValue).floatValue();
                    float f = 1.0f - fFloatValue;
                    float f2 = fFloatValue * 1.0f;
                    allAppsPagedCellLayout.setScaleX((AllAppsPagedView.mCellLayout_middle_scale_xFactor * f) + f2);
                    allAppsPagedCellLayout.setScaleY((AllAppsPagedView.mCellLayout_middle_scale_yFactor * f) + f2);
                    allAppsPagedCellLayout.setTranslationY(0.0f);
                    allAppsPagedCellLayout.setBGAlpha(fFloatValue > 1.0f ? 0 : (int) (f * 255.0f));
                    allAppsPagedCellLayout.invalidate();
                    if (iMax != id && allAppsPagedCellLayout2 != null) {
                        if (!AllAppsPagedView.this.mIsRtl) {
                            allAppsPagedCellLayout2.setTranslationX((-measuredWidth) * fFloatValue);
                            allAppsPagedCellLayout2.setTranslationY(0.0f);
                        } else {
                            allAppsPagedCellLayout2.setTranslationX(measuredWidth * fFloatValue);
                            allAppsPagedCellLayout2.setTranslationY(0.0f);
                        }
                    }
                    if (iMin == id || allAppsPagedCellLayout3 == null) {
                        return;
                    }
                    if (!AllAppsPagedView.this.mIsRtl) {
                        allAppsPagedCellLayout3.setTranslationX(measuredWidth * fFloatValue);
                        allAppsPagedCellLayout3.setTranslationY(0.0f);
                    } else {
                        allAppsPagedCellLayout3.setTranslationX((-measuredWidth) * fFloatValue);
                        allAppsPagedCellLayout3.setTranslationY(0.0f);
                    }
                }
            });
            this.scaleAni.addListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.7
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    AllAppsPagedView.this.mIsScaleAni_Canceled = false;
                    AllAppsPagedView.this.setHardwareLayer(false);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    AllAppsPagedView.this.mIsScaleAni_Canceled = true;
                    AllAppsPagedView.this.setHardwareLayer(true);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    AllAppsPagedView.this.mLayoutScale = 1.0f;
                    AllAppsPagedView allAppsPagedView = AllAppsPagedView.this;
                    allAppsPagedView.setPageSpacing((int) allAppsPagedView.mDefault_Spacing);
                    AllAppsPagedView.this.setScale(false);
                    AllAppsPagedView.this.scaleAni = null;
                    AllAppsPagedView allAppsPagedView2 = AllAppsPagedView.this;
                    AllAppsPagedCellLayout allAppsPagedCellLayout4 = (AllAppsPagedCellLayout) allAppsPagedView2.getChildAt(allAppsPagedView2.mCurrentPage);
                    if (allAppsPagedCellLayout4 != null) {
                        allAppsPagedCellLayout4.setShrinkEffect(false);
                        allAppsPagedCellLayout4.invalidate();
                    }
                    AllAppsPagedView.this.invalidate();
                    if (!AllAppsPagedView.this.mIsScaleAni_Canceled) {
                        AllAppsPagedView.this.mArrangeMode = false;
                        AllAppsPagedView.this.updateFeatureEnabled();
                    } else {
                        AllAppsPagedView.this.mIsScaleAni_Canceled = false;
                    }
                    AllAppsPagedView.this.setHardwareLayer(true);
                    AllAppsPagedView.this.setChildFocus();
                }
            });
            this.scaleAni.start();
            setChildShrinkEffect(true);
            return;
        }
        this.mLayoutScale = 1.0f;
        setPageSpacing((int) this.mDefault_Spacing);
        setScale(false);
        this.scaleAni = null;
        AllAppsPagedCellLayout allAppsPagedCellLayout4 = (AllAppsPagedCellLayout) getChildAt(this.mCurrentPage);
        if (allAppsPagedCellLayout4 != null) {
            allAppsPagedCellLayout4.setShrinkEffect(false);
            allAppsPagedCellLayout4.invalidate();
        }
        invalidate();
        if (!this.mIsScaleAni_Canceled) {
            this.mArrangeMode = false;
            updateFeatureEnabled();
        } else {
            this.mIsScaleAni_Canceled = false;
        }
        setHardwareLayer(true);
        setChildFocus();
    }

    private void setupDragMode() {
        this.mArrangeMode = true;
        updateFeatureEnabled();
        this.mDragController.setDragScoller(this);
        this.mDragController.setMoveTarget(this);
        setClipChildrenAtPage(this.mCurrentPage, false);
        destroyHardwareLayerCreation();
    }

    private void tearDownDragMode() {
        clearAllHovers();
        final int i = this.mCurrentPage;
        Runnable runnable = new Runnable() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.8
            @Override // java.lang.Runnable
            public void run() {
                AllAppsPagedView.this.setClipChildrenAtPage(i, true);
                AllAppsPagedView.this.invalidate();
            }
        };
        this.mRunnable = runnable;
        postDelayed(runnable, 500L);
    }

    boolean startArrangeMode(boolean animated) {
        if (isInArrangeMode() || ((getVisibility() == 0 && !this.mScroller.isFinished()) || this.mNextPage != -1)) {
            return false;
        }
        this.mArrangeMode = true;
        this.mLauncher.lockScreenOrientation();
        updateFeatureEnabled();
        setClipChildren(false);
        setClipToPadding(false);
        cancelChildLongPress();
        shrink(animated);
        this.mDragController.removeDragListener(this.mLauncher.getWorkspace());
        this.mDragController.addDropTarget(this);
        this.mDragController.addDragListener(this);
        setPageArrangeModeBg(true, this.mCurrentPage);
        setPageArrangeModeBg(true, this.mCurrentPage != 0 ? this.mCurrentPage - 1 : 0);
        setPageArrangeModeBg(true, this.mCurrentPage == getChildCount() - 1 ? this.mCurrentPage : this.mCurrentPage + 1);
        if (TalkBackUtils.isEnabled(getContext())) {
            announceForAccessibility(this.mContext.getString(R.string.all_apps_button_label) + "," + this.mContext.getString(R.string.sp_editing_NORMAL));
        }
        UninstallModeManager.getInstance(this.mContext).enterUninstallMode(this);
        return true;
    }

    protected void setPageArrangeModeBg(boolean newMode, int page) {
        AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(page);
        if (allAppsPagedCellLayout == null || allAppsPagedCellLayout.isInArrangeMode() == newMode) {
            return;
        }
        int pageChildCount = allAppsPagedCellLayout.getPageChildCount();
        for (int i = 0; i < pageChildCount; i++) {
            View childOnPageAt = allAppsPagedCellLayout.getChildOnPageAt(i);
            if (childOnPageAt != null) {
                if (childOnPageAt instanceof BubbleTextView) {
                    ((BubbleTextView) childOnPageAt).invalidate();
                } else if (childOnPageAt instanceof FolderIcon) {
                    ((FolderIcon) childOnPageAt).invalidate();
                }
            }
        }
        allAppsPagedCellLayout.setArrangeModeBg(newMode);
    }

    boolean endArrangeMode(boolean animated) {
        if (!isInArrangeMode() || ((getVisibility() == 0 && !this.mScroller.isFinished()) || this.mNextPage != -1)) {
            return false;
        }
        this.mArrangeMode = false;
        this.mLauncher.unlockScreenOrientation(true);
        updateFeatureEnabled();
        setClipChildren(true);
        setClipToPadding(true);
        cancelChildLongPress();
        unshrink(animated);
        this.mDragController.addDragListener(this.mLauncher.getWorkspace());
        this.mDragController.removeDropTarget(this);
        this.mDragController.removeDragListener(this);
        this.mDragController.setDragScoller(this.mLauncher.getWorkspace());
        setPageArrangeModeBg(true, this.mCurrentPage);
        setPageArrangeModeBg(true, this.mCurrentPage == 0 ? 0 : this.mCurrentPage - 1);
        setPageArrangeModeBg(true, this.mCurrentPage == getChildCount() - 1 ? this.mCurrentPage : this.mCurrentPage + 1);
        setArrangeModeBg(false);
        setChildFocus();
        UninstallModeManager.getInstance(this.mContext).exitUninstallMode(this);
        return true;
    }

    private void addVacantPage() {
        AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(getChildCount() - 1);
        if (allAppsPagedCellLayout == null || allAppsPagedCellLayout.getPageChildCount() <= 0) {
            return;
        }
        AllAppsPagedCellLayout allAppsPagedCellLayout2 = new AllAppsPagedCellLayout(getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -1);
        setupPage(allAppsPagedCellLayout2);
        addView(allAppsPagedCellLayout2, layoutParams);
        allAppsPagedCellLayout2.enableCenteredContent(false);
        allAppsPagedCellLayout2.setShrinkEffect(true);
        this.mFirstLayout = true;
    }

    public void removeVacantPage() {
        int childCount = getChildCount();
        if (this.mArrangeMode) {
            return;
        }
        int i = 0;
        boolean z = false;
        while (i < childCount) {
            AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(i);
            if (allAppsPagedCellLayout != null && allAppsPagedCellLayout.getPageChildCount() == 0) {
                removeView(allAppsPagedCellLayout);
                for (int i2 = i; i2 < getChildCount(); i2++) {
                    AllAppsPagedCellLayout allAppsPagedCellLayout2 = (AllAppsPagedCellLayout) getChildAt(i2);
                    if (allAppsPagedCellLayout2 != null) {
                        for (int i3 = 0; i3 < allAppsPagedCellLayout2.getPageChildCount(); i3++) {
                            View childOnPageId = allAppsPagedCellLayout2.getChildOnPageId(i3);
                            if (childOnPageId != null) {
                                ItemInfo itemInfo = (ItemInfo) childOnPageId.getTag();
                                long j = i2;
                                if (itemInfo.screenId != j) {
                                    itemInfo.screenId = j;
                                    itemInfo.requiresDbUpdate = true;
                                    z = true;
                                }
                            }
                        }
                    }
                }
                i--;
            }
            i++;
        }
        setCurrentPage(this.mCurrentPage);
        requestLayout();
        if (z) {
            this.mMenuItemFactory.updatePositionChangedItems();
        }
    }

    @Override // com.lge.launcher3.PagedView, android.view.ViewGroup, android.view.ViewManager
    public void removeView(View view) {
        AllAppsPagedCellLayout allAppsPagedCellLayout;
        if (view == null) {
            return;
        }
        super.removeView(view);
        int id = view.getId();
        int childCount = getChildCount();
        if (id == -1 && (allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(this.mCurrentPage)) != null) {
            id = allAppsPagedCellLayout.getId();
        }
        while (true) {
            id++;
            if (id > childCount) {
                return;
            }
            View childAt = getChildAt(id);
            if (childAt != null) {
                childAt.setId(id - 1);
            }
        }
    }

    private boolean insertAnotherPage(View view, int pageto, int index, boolean banimated) {
        AllAppsPagedCellLayoutParam allAppsPagedCellLayoutParam;
        View view2;
        int i;
        int i2;
        View childOnPageId;
        final AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(pageto);
        LGLog.d("[PageMenu]", "INSERT ANOTHER PAGE TO= " + pageto + "INDEX =" + index);
        ArrayList arrayList = new ArrayList();
        if (allAppsPagedCellLayout == null) {
            return false;
        }
        int pageChildCount = allAppsPagedCellLayout.getPageChildCount();
        if (pageChildCount == this.mCellCountX * this.mCellCountY && (childOnPageId = allAppsPagedCellLayout.getChildOnPageId(pageChildCount - 1)) != null) {
            allAppsPagedCellLayout.removeViewOnPageId(i2);
            int i3 = pageto + 1;
            if (getChildAt(i3) == null) {
                addVacantPage();
            }
            insertAnotherPage(childOnPageId, i3, 0, false);
            pageChildCount--;
        }
        if (banimated) {
            int i4 = 0;
            int i5 = pageChildCount - 1;
            while (i5 >= index) {
                View childOnPageId2 = allAppsPagedCellLayout.getChildOnPageId(i5);
                if (childOnPageId2 == null) {
                    i = i5;
                } else {
                    AllAppsPagedCellLayoutParam allAppsPagedCellLayoutParam2 = (AllAppsPagedCellLayoutParam) childOnPageId2.getLayoutParams();
                    int cellHeight = (allAppsPagedCellLayout.getCellHeight() + allAppsPagedCellLayout.getHeightGap()) * (i5 / this.mCellCountX);
                    int cellWidth = (allAppsPagedCellLayout.getCellWidth() + allAppsPagedCellLayout.getWidthGap()) * (i5 % this.mCellCountX);
                    if (allAppsPagedCellLayoutParam2.cellX == this.mCellCountX - 1) {
                        allAppsPagedCellLayoutParam = allAppsPagedCellLayoutParam2;
                        view2 = childOnPageId2;
                        i = i5;
                        arrayList.add(allAppsPagedCellLayout.getAnimator(childOnPageId2, cellWidth, 0, cellHeight, cellHeight + allAppsPagedCellLayout.getCellHeight() + allAppsPagedCellLayout.getHeightGap(), i4));
                    } else {
                        allAppsPagedCellLayoutParam = allAppsPagedCellLayoutParam2;
                        view2 = childOnPageId2;
                        i = i5;
                        arrayList.add(allAppsPagedCellLayout.getAnimator(view2, cellWidth, allAppsPagedCellLayout.getCellWidth() + cellWidth + allAppsPagedCellLayout.getWidthGap(), cellHeight, cellHeight, i4));
                    }
                    i4 += 10;
                    int i6 = i + 1;
                    allAppsPagedCellLayoutParam.cellX = i6 % this.mCellCountX;
                    allAppsPagedCellLayoutParam.cellY = i6 / this.mCellCountX;
                    ((AllAppsItemInfo) view2.getTag()).cellX = i6 % this.mCellCountX;
                    ((AllAppsItemInfo) view2.getTag()).cellY = i6 / this.mCellCountX;
                    ((AllAppsItemInfo) view2.getTag()).requiresDbUpdate = true;
                    view2.setId(i6);
                }
                i5 = i - 1;
            }
            if (!arrayList.isEmpty()) {
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.setDuration(200L);
                animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.9
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationRepeat(Animator arg0) {
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator arg0) {
                        allAppsPagedCellLayout.enableHardwareLayer(false);
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator arg0) {
                        allAppsPagedCellLayout.enableHardwareLayer(true);
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator arg0) {
                        allAppsPagedCellLayout.enableHardwareLayer(true);
                    }
                });
                animatorSet.playTogether(arrayList);
                animatorSet.start();
            }
        } else {
            for (int i7 = pageChildCount - 1; i7 >= index; i7--) {
                View childOnPageId3 = allAppsPagedCellLayout.getChildOnPageId(i7);
                if (childOnPageId3 != null) {
                    AllAppsPagedCellLayoutParam allAppsPagedCellLayoutParam3 = (AllAppsPagedCellLayoutParam) childOnPageId3.getLayoutParams();
                    int i8 = i7 + 1;
                    allAppsPagedCellLayoutParam3.cellX = i8 % this.mCellCountX;
                    allAppsPagedCellLayoutParam3.cellY = i8 / this.mCellCountX;
                    allAppsPagedCellLayoutParam3.isLockedToGrid = true;
                    childOnPageId3.setTranslationX(0.0f);
                    childOnPageId3.setTranslationY(0.0f);
                    childOnPageId3.requestLayout();
                    ((AllAppsItemInfo) childOnPageId3.getTag()).cellX = i8 % this.mCellCountX;
                    ((AllAppsItemInfo) childOnPageId3.getTag()).cellY = i8 / this.mCellCountX;
                    ((AllAppsItemInfo) childOnPageId3.getTag()).requiresDbUpdate = true;
                    childOnPageId3.setId(i8);
                }
            }
        }
        if (view != null) {
            view.setTranslationX(0.0f);
            view.setTranslationY(0.0f);
            allAppsPagedCellLayout.addViewToCellLayout(view, -1, index, new AllAppsPagedCellLayoutParam(index % this.mCellCountX, index / this.mCellCountX, 1, 1));
            ((AllAppsItemInfo) view.getTag()).cellX = index % this.mCellCountX;
            ((AllAppsItemInfo) view.getTag()).cellY = index / this.mCellCountX;
            ((AllAppsItemInfo) view.getTag()).screenId = pageto;
            ((AllAppsItemInfo) view.getTag()).requiresDbUpdate = true;
        }
        allAppsPagedCellLayout.getShortcutsAndWidgets().requestLayout();
        return true;
    }

    @Override // com.android.launcher3.DropTarget
    public void onDragEnter(DropTarget.DragObject dragObject) {
        AllAppsPagedCellLayout allAppsPagedCellLayout;
        int i;
        if (dragObject.dragInfo instanceof ShortcutInfo) {
            setupDragMode();
            this.mItemFromFolder = (ShortcutInfo) dragObject.dragInfo;
            AppInfo appInfo = this.mAppUtil.getAppInfo(((ShortcutInfo) dragObject.dragInfo).intent.getComponent(), ((ShortcutInfo) dragObject.dragInfo).user);
            if (appInfo == null || (allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(this.mCurrentPage)) == null) {
                return;
            }
            float[] fArr = new float[2];
            getDragViewVisualCenter(dragObject.x, dragObject.y, dragObject.xOffset, dragObject.yOffset, dragObject.dragView, fArr);
            float descendantCoordRelativeToSelf = ViewPosition.getDescendantCoordRelativeToSelf(this.mLauncher.getDragLayer(), allAppsPagedCellLayout, new int[2]);
            if (r2[0] > fArr[0] || r2[0] + (allAppsPagedCellLayout.getWidth() * descendantCoordRelativeToSelf) < fArr[0] || r2[1] > fArr[1] || r2[1] + (descendantCoordRelativeToSelf * allAppsPagedCellLayout.getHeight()) < fArr[1]) {
                return;
            }
            getInverseMappoint(fArr);
            int index = allAppsPagedCellLayout.getIndex((int) fArr[0], (int) fArr[1], -1, this.mIsPortrait);
            if (index == -1 || (i = index & 255) < 0 || i >= this.mCellCountX * this.mCellCountY) {
                return;
            }
            AllAppsItemInfo allAppsItemInfoAddApplication = addApplication(appInfo, this.mCurrentPage, i);
            this.mDragItemFromFolder = allAppsItemInfoAddApplication;
            if (allAppsItemInfoAddApplication != null) {
                dragObject.dragInfo = allAppsItemInfoAddApplication;
                this.mDragItemFromFolder.itemView.setVisibility(4);
                this.mdragIndex = this.mDragItemFromFolder.itemView.getId();
                this.mdragPage = this.mCurrentPage;
                ShortcutInfo shortcutInfo = this.mItemFromFolder;
                if (shortcutInfo != null) {
                    this.mMenuItemFactory.removeFolderItem(null, shortcutInfo);
                    this.mItemFromFolder = null;
                }
            }
        }
    }

    @Override // com.android.launcher3.DropTarget
    public void onDragExit(DropTarget.DragObject dragObject) {
        LGLog.d(TAG, "onDragExit() : " + dragObject.dragInfo);
        if (this.mLastFolderAniIdx != -1) {
            View childOnPageId = ((AllAppsPagedCellLayout) getChildAt(this.mCurrentPage)).getChildOnPageId(this.mLastFolderAniIdx);
            if (childOnPageId == null) {
                return;
            }
            if (childOnPageId instanceof FolderIcon) {
                ((FolderIcon) childOnPageId).onDragExit(null);
            }
        }
        cleanupFolderCreation();
    }

    @Override // com.android.launcher3.DropTarget
    public void onDragOver(DropTarget.DragObject dragObject) {
        View childOnPageId;
        View childOnPageId2;
        AppInfo appInfo;
        int paddingRight;
        if ((dragObject.dragInfo instanceof ShortcutInfo) && (appInfo = this.mAppUtil.getAppInfo(((ShortcutInfo) dragObject.dragInfo).intent.getComponent(), ((ShortcutInfo) dragObject.dragInfo).user)) != null) {
            AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(this.mCurrentPage);
            if (allAppsPagedCellLayout == null) {
                return;
            }
            float[] fArr = new float[2];
            getDragViewVisualCenter(dragObject.x, dragObject.y, dragObject.xOffset, dragObject.yOffset, dragObject.dragView, fArr);
            getInverseMappoint(fArr);
            if (!this.mIsRtl) {
                paddingRight = (this.mCurrentPage * getPaddingLeft()) / 2;
            } else {
                paddingRight = ((this.mCurrentPage * getPaddingRight()) / 2) - (getPaddingLeft() / 2);
            }
            int index = allAppsPagedCellLayout.getIndex(((int) fArr[0]) - (paddingRight + ((int) (getScrollX() * ((1.0f - mCellLayout_middle_scale_yFactor) + 1.0f)))), (int) fArr[1], -1, this.mIsPortrait) & 255;
            if (index >= 0 && index < this.mCellCountX * this.mCellCountY) {
                AllAppsItemInfo allAppsItemInfoAddApplication = addApplication(appInfo, this.mCurrentPage, index);
                this.mDragItemFromFolder = allAppsItemInfoAddApplication;
                if (allAppsItemInfoAddApplication != null) {
                    dragObject.dragInfo = allAppsItemInfoAddApplication;
                    this.mDragItemFromFolder.itemView.setVisibility(4);
                    this.mdragIndex = this.mDragItemFromFolder.itemView.getId();
                    this.mdragPage = this.mCurrentPage;
                    ShortcutInfo shortcutInfo = this.mItemFromFolder;
                    if (shortcutInfo != null) {
                        this.mMenuItemFactory.removeFolderItem(null, shortcutInfo);
                        this.mItemFromFolder = null;
                    }
                }
            }
        }
        AllAppsPagedCellLayout allAppsPagedCellLayout2 = (AllAppsPagedCellLayout) getChildAt(this.mCurrentPage);
        if (!this.mScroller.isFinished() || allAppsPagedCellLayout2 == null) {
            return;
        }
        float[] fArr2 = new float[2];
        getDragViewVisualCenter(dragObject.x, dragObject.y, dragObject.xOffset, dragObject.yOffset, dragObject.dragView, fArr2);
        getInverseMappoint(fArr2);
        int index2 = allAppsPagedCellLayout2.getIndex(((int) fArr2[0]) - ((!this.mIsRtl ? (this.mCurrentPage * getPaddingLeft()) / 2 : ((this.mCurrentPage * getPaddingRight()) / 2) - (getPaddingLeft() / 2)) + ((int) (getScrollX() * ((1.0f - mCellLayout_middle_scale_yFactor) + 1.0f)))), (int) fArr2[1], this.mdragIndex, this.mIsPortrait);
        int i = this.mdragPage;
        if (i != -1 && i != this.mCurrentPage) {
            if (index2 != -1 && isInCellLayout(dragObject.x, dragObject.y, this.mCurrentPage)) {
                AllAppsPagedCellLayout allAppsPagedCellLayout3 = (AllAppsPagedCellLayout) getChildAt(this.mdragPage);
                if (allAppsPagedCellLayout3 == null || (childOnPageId2 = allAppsPagedCellLayout3.getChildOnPageId(this.mdragIndex)) == null) {
                    return;
                }
                allAppsPagedCellLayout3.removeNarrangePage(this.mdragIndex, false);
                int i2 = index2 & 255;
                insertAnotherPage(childOnPageId2, this.mCurrentPage, i2, true);
                this.mdragIndex = i2;
                this.mdragPage = this.mCurrentPage;
                this.mswapIndex = -1;
            }
        } else if (index2 != -1 && (index2 & 255) != this.mdragIndex && this.mswapIndex != index2) {
            this.mswapIndex = index2;
            this.mReArrangeAlarm.cancelAlarm();
            this.mReArrangeAlarm.setAlarm(190L);
            this.mReArrangeAlarm.setOnAlarmListener(this.mReArrangeAlarmListener);
        }
        int i3 = this.mLastFolderAniIdx;
        if (i3 == -1 || (i3 | 512) == index2 || (childOnPageId = allAppsPagedCellLayout2.getChildOnPageId(i3)) == null) {
            return;
        }
        if (((ItemInfo) childOnPageId.getTag()).itemType == 0) {
            cleanupFolderCreation();
        } else {
            ((FolderIcon) childOnPageId).onDragExit(dragObject.dragInfo);
        }
        this.mLastFolderAniIdx = -1;
        this.mswapIndex = -1;
    }

    @Override // com.android.launcher3.DropTarget
    public void onDrop(DropTarget.DragObject dragObject) {
        AllAppsPagedCellLayout allAppsPagedCellLayout;
        View childOnPageId;
        View view;
        this.mReArrangeAlarm.cancelAlarm();
        if ((dragObject.dragInfo instanceof AllAppsItemInfo) && (view = ((AllAppsItemInfo) dragObject.dragInfo).itemView) != null) {
            ((AllAppsPagedCellLayoutParam) view.getLayoutParams()).isLockedToGrid = true;
            view.requestLayout();
            view.setTranslationX(0.0f);
            view.setTranslationY(0.0f);
        }
        if (!this.mScroller.isFinished()) {
            LGLog.d("[PageMenu]", "ONDROP is occured During mScroller is not finished");
            AllAppsPagedCellLayout allAppsPagedCellLayout2 = (AllAppsPagedCellLayout) getChildAt(this.mNextPage);
            if (allAppsPagedCellLayout2 != null && (allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(this.mdragPage)) != null && (childOnPageId = allAppsPagedCellLayout.getChildOnPageId(this.mdragIndex)) != null) {
                if (allAppsPagedCellLayout2.getPageChildCount() >= this.mCellCountX * this.mCellCountY) {
                    dragObject.deferDragViewCleanupPostAnimation = false;
                    childOnPageId.setVisibility(0);
                    return;
                }
                if (this.mdragPage != this.mNextPage) {
                    int pageChildCount = allAppsPagedCellLayout2.getPageChildCount() | 256;
                    allAppsPagedCellLayout.endAnimation();
                    allAppsPagedCellLayout.removeNarrangePage(this.mdragIndex, false);
                    allAppsPagedCellLayout.requestLayout();
                    int i = pageChildCount & 255;
                    insertAnotherPage(childOnPageId, this.mNextPage, i, true);
                    this.mdragIndex = i;
                    this.mdragPage = this.mNextPage;
                    this.mCurrentPage = this.mNextPage;
                    setCurrentPage(this.mCurrentPage);
                    this.bAllowSwap = false;
                    this.mswapIndex = -1;
                }
            }
        }
        AllAppsPagedCellLayout allAppsPagedCellLayout3 = (AllAppsPagedCellLayout) getChildAt(this.mCurrentPage);
        AllAppsPagedCellLayout allAppsPagedCellLayout4 = (AllAppsPagedCellLayout) getChildAt(this.mdragPage);
        if (allAppsPagedCellLayout3 != null && allAppsPagedCellLayout4 != null) {
            float[] fArr = new float[2];
            getDragViewVisualCenter(dragObject.x, dragObject.y, dragObject.xOffset, dragObject.yOffset, dragObject.dragView, fArr);
            getInverseMappoint(fArr);
            allAppsPagedCellLayout3.getIndex((int) fArr[0], (int) fArr[1], this.mdragIndex, this.mIsPortrait);
            int i2 = this.mswapIndex;
            View childOnPageId2 = allAppsPagedCellLayout3.getChildOnPageId(i2 & 255);
            View childOnPageId3 = allAppsPagedCellLayout4.getChildOnPageId(this.mdragIndex);
            if ((i2 & 65280) != 512 || this.bAllowSwap || childOnPageId3 == null) {
                if (childOnPageId3 != null) {
                    dropAnimation(dragObject.dragView, childOnPageId3);
                } else if ((dragObject.dragSource instanceof Folder) && (dragObject.dragInfo instanceof AllAppsItemInfo)) {
                    ((Folder) dragObject.dragSource).onAdd(new ShortcutInfo((AllAppsItemInfo) dragObject.dragInfo));
                } else {
                    dragObject.deferDragViewCleanupPostAnimation = false;
                    LGLog.e("[PageMenu]", "DragSrc is null Drop Animation could not appear");
                }
            } else if (childOnPageId2 != null && !childOnPageId2.equals(childOnPageId3)) {
                ItemInfo itemInfo = (ItemInfo) childOnPageId3.getTag();
                ItemInfo itemInfo2 = (ItemInfo) childOnPageId2.getTag();
                if (itemInfo == null || itemInfo2 == null) {
                    dragObject.deferDragViewCleanupPostAnimation = false;
                    return;
                }
                if (itemInfo.itemType == 2) {
                    dropAnimation(dragObject.dragView, childOnPageId3);
                    return;
                }
                if (itemInfo2.itemType == 2) {
                    if (this.mLastFolderAniIdx != -1) {
                        addAppToFolder(childOnPageId2, childOnPageId3, true, dragObject);
                        this.mLastFolderAniIdx = -1;
                        return;
                    } else {
                        dropAnimation(dragObject.dragView, childOnPageId3);
                        return;
                    }
                }
                if (itemInfo2.itemType == 0) {
                    if (this.mLastFolderAniIdx != -1) {
                        AllAppsPagedCellLayoutParam allAppsPagedCellLayoutParam = (AllAppsPagedCellLayoutParam) childOnPageId2.getLayoutParams();
                        if ((allAppsPagedCellLayoutParam.cellY * this.mCellCountX) + allAppsPagedCellLayoutParam.cellX <= allAppsPagedCellLayout3.getShortcutsAndWidgets().getChildCount()) {
                            makeFolderNadd(childOnPageId2, childOnPageId3, !Utilities.isPowerSaveMode(this.mContext), dragObject);
                            this.mLastFolderAniIdx = -1;
                            return;
                        } else {
                            dropAnimation(dragObject.dragView, childOnPageId3);
                            return;
                        }
                    }
                    dropAnimation(dragObject.dragView, childOnPageId3);
                    return;
                }
            } else if (childOnPageId3 != null) {
                dropAnimation(dragObject.dragView, childOnPageId3);
            }
        }
        if (dragObject.dragSource instanceof Folder) {
            tearDownDragMode();
        }
        this.mMenuItemFactory.updatePositionChangedItems();
    }

    @Override // com.android.launcher3.DragSource
    public void onDropCompleted(View targetView, DropTarget.DragObject dragObject, boolean isFlingToDelete, boolean success) {
        if (!(dragObject.dragInfo instanceof AllAppsItemInfo)) {
            if (targetView instanceof DeleteDropTarget) {
                return;
            }
            this.mLauncher.exitSpringLoadedDragModeDelayed(true, 0, null);
            return;
        }
        if (!success && (targetView instanceof Workspace)) {
            dragObject.deferDragViewCleanupPostAnimation = false;
            this.mLauncher.exitSpringLoadedDragModeDelayed(true, 0, null);
        }
        if (targetView instanceof AllAppsFolder) {
            AllAppsItemInfo allAppsItemInfo = (AllAppsItemInfo) dragObject.dragInfo;
            this.mMenuItemFactory.removeItemInfo(allAppsItemInfo);
            removeItemInAllApps(allAppsItemInfo.itemView, allAppsItemInfo.screenId);
        }
        View view = ((AllAppsItemInfo) dragObject.dragInfo).itemView;
        if (view != null) {
            ((AllAppsPagedCellLayoutParam) view.getLayoutParams()).isLockedToGrid = true;
            view.requestLayout();
            view.setTranslationX(0.0f);
            view.setTranslationY(0.0f);
        }
        if (!success && ((AllAppsItemInfo) dragObject.dragInfo).itemView != null) {
            if (targetView instanceof AllAppsPagedView) {
                dropAnimation(dragObject.dragView, ((AllAppsItemInfo) dragObject.dragInfo).itemView);
            } else if (targetView == null) {
                if (this.mLauncher.isInState(LauncherState.APPS_SPRING_LOADED)) {
                    dragObject.deferDragViewCleanupPostAnimation = false;
                    this.mLauncher.exitSpringLoadedDragModeDelayed(true, 0, null);
                } else if (this.mLauncher.isInState(LauncherState.ALL_APPS) && isInArrangeMode()) {
                    dropAnimation(dragObject.dragView, ((AllAppsItemInfo) dragObject.dragInfo).itemView);
                }
            }
        }
        tearDownDragMode();
        if (!(targetView instanceof Workspace) && (this.mdragPage == -1 || this.mdragIndex == -1)) {
            LGLog.e(TAG, " Some Icons could be disappeared.");
        }
        if (this.mLastFolderAniIdx != -1) {
            View childAt = getChildAt(this.mCurrentPage, this.mLastFolderAniIdx);
            if (childAt != null) {
                if (((ItemInfo) childAt.getTag()).itemType == 0) {
                    cancelMakeFolder(true);
                } else {
                    ((FolderIcon) childAt).onDragExit(dragObject.dragInfo);
                }
            }
            this.mLastFolderAniIdx = -1;
        }
        this.mdragIndex = -1;
        this.mdragPage = -1;
        this.mswapIndex = -1;
        this.mLastFolderAniIdx = -1;
        this.bAllowSwap = false;
        requestLayout();
    }

    private View getChildAt(int pageIndex, int id) {
        AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(pageIndex);
        if (allAppsPagedCellLayout == null || id >= allAppsPagedCellLayout.getPageChildCount()) {
            return null;
        }
        return allAppsPagedCellLayout.getChildOnPageId(this.mLastFolderAniIdx);
    }

    private void addAppToFolder(View folder, View app, boolean bAnimated, DropTarget.DragObject d) {
        FolderIcon folderIcon = (FolderIcon) folder;
        FolderInfo folderInfo = folderIcon.getFolderInfo();
        AllAppsItemInfo allAppsItemInfo = (AllAppsItemInfo) app.getTag();
        this.mMenuItemFactory.removeItemInfo((AllAppsItemInfo) app.getTag());
        final AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(this.mdragPage);
        if (allAppsPagedCellLayout != null) {
            if (bAnimated) {
                final int i = this.mdragIndex;
                d.postAnimationRunnable = new Runnable() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.10
                    @Override // java.lang.Runnable
                    public void run() {
                        AllAppsPagedCellLayout allAppsPagedCellLayout2 = allAppsPagedCellLayout;
                        if (allAppsPagedCellLayout2 != null) {
                            allAppsPagedCellLayout2.removeNarrangePage(i, true);
                        }
                        AllAppsPagedView.this.mMenuItemFactory.updatePositionChangedItems();
                    }
                };
                folderIcon.onDrop(d);
            } else {
                folderInfo.add(new ShortcutInfo(allAppsItemInfo));
                allAppsPagedCellLayout.removeNarrangePage(this.mdragIndex, false);
                this.mMenuItemFactory.updatePositionChangedItems();
            }
        }
    }

    private void makeFolderNadd(View target, View drag, boolean bAnimated, DropTarget.DragObject d) {
        cancelMakeFolder(false);
        Rect rect = new Rect();
        float descendantRectRelativeToSelf = ViewPosition.getDescendantRectRelativeToSelf(this.mLauncher.getDragLayer(), target, rect);
        AllAppsItemInfo allAppsItemInfoMakeApplicationToFolder = makeApplicationToFolder((BubbleTextView) target);
        if (allAppsItemInfoMakeApplicationToFolder == null) {
            return;
        }
        FolderIcon folderIcon = (FolderIcon) allAppsItemInfoMakeApplicationToFolder.itemView;
        FolderInfo folderInfo = folderIcon.getFolderInfo();
        AllAppsItemInfo allAppsItemInfo = (AllAppsItemInfo) drag.getTag();
        folderIcon.setOnClickListener(this.mOnClickListener);
        folderIcon.setOnLongClickListener(this.mOnLongClickListener);
        this.mMenuItemFactory.removeItemInfo((AllAppsItemInfo) drag.getTag());
        final AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(this.mdragPage);
        if (allAppsPagedCellLayout != null) {
            if (bAnimated) {
                final int i = this.mdragIndex;
                folderIcon.performCreateAnimation(((AppInfo) target.getTag()).makeShortcut(), target, ((AppInfo) d.dragInfo).makeShortcut(), d.dragView, rect, descendantRectRelativeToSelf, new Runnable() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.11
                    @Override // java.lang.Runnable
                    public void run() {
                        allAppsPagedCellLayout.removeNarrangePage(i, true);
                        AllAppsPagedView.this.mMenuItemFactory.updatePositionChangedItems();
                    }
                });
            } else {
                folderInfo.add(new ShortcutInfo(allAppsItemInfo));
                allAppsPagedCellLayout.removeNarrangePage(this.mdragIndex, false);
                this.mMenuItemFactory.updatePositionChangedItems();
                d.dragView.setVisibility(4);
            }
        }
    }

    @Override // com.android.launcher3.DropTarget
    public void getLocationInDragLayer(int[] loc) {
        loc[1] = 0;
        loc[0] = 0;
        getLocationOnScreen(loc);
    }

    @Override // com.android.launcher3.logging.UserEventDispatcher.LogContainerProvider
    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {
        target.gridX = info.cellX;
        target.gridY = info.cellY;
        target.pageIndex = getCurrentPage();
        targetParent.containerType = 1;
        if (info.container == -101) {
            target.rank = info.rank;
            targetParent.containerType = 2;
        } else if (info.container >= 0) {
            targetParent.containerType = 3;
        }
    }

    @Override // com.android.launcher3.DropTarget
    public boolean acceptDrop(DropTarget.DragObject dragObject) {
        return !(dragObject.dragInfo instanceof ShortcutInfo);
    }

    @Override // android.view.View
    public void getHitRect(Rect outRect) {
        if (isInArrangeMode()) {
            Display defaultDisplay = this.mLauncher.getWindowManager().getDefaultDisplay();
            Point point = new Point();
            defaultDisplay.getSize(point);
            outRect.set(0, 0, point.x, point.y);
            return;
        }
        super.getHitRect(outRect);
    }

    @Override // com.android.launcher3.DragScroller
    public boolean onEnterScrollArea(int x, int y, int direction) {
        if (getOpenFolder() != null) {
            return false;
        }
        AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(this.mCurrentPage);
        Rect rect = new Rect();
        if (allAppsPagedCellLayout != null) {
            ViewPosition.getDescendantRectRelativeToSelf(this.mLauncher.getDragLayer(), allAppsPagedCellLayout, rect);
        }
        if (y <= rect.top || y >= rect.bottom) {
            return false;
        }
        if (direction == 0) {
            this.mDragState = DragState.SCROLL_LEFT;
            invalidate();
        } else if (direction == 1) {
            this.mDragState = DragState.SCROLL_RIGHT;
            invalidate();
        }
        return true;
    }

    @Override // com.android.launcher3.DragScroller
    public boolean onExitScrollArea() {
        clearAllHovers();
        invalidate();
        return true;
    }

    private void clearAllHovers() {
        this.mDragState = DragState.NONE;
        invalidate();
    }

    private void setChildShrinkEffect(boolean bEffect) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(i);
            if (allAppsPagedCellLayout != null) {
                allAppsPagedCellLayout.setShrinkEffect(bEffect);
            }
        }
    }

    @Override // com.lge.launcher3.PagedView, android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        AllAppsPagedCellLayout allAppsPagedCellLayout;
        AllAppsPagedCellLayout allAppsPagedCellLayout2;
        Drawable drawable;
        Drawable drawable2;
        super.dispatchDraw(canvas);
        if (isInArrangeMode()) {
            int measuredWidth = getMeasuredWidth();
            int scrollX = getScrollX() + (measuredWidth / 2);
            if (!isScaleAniRunning()) {
                screenScrolled(scrollX);
            }
            if (!this.mIsRtl) {
                allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(this.mCurrentPage - 1);
            } else {
                allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(this.mCurrentPage + 1);
            }
            if (!this.mIsRtl) {
                allAppsPagedCellLayout2 = (AllAppsPagedCellLayout) getChildAt(this.mCurrentPage + 1);
            } else {
                allAppsPagedCellLayout2 = (AllAppsPagedCellLayout) getChildAt(this.mCurrentPage - 1);
            }
            if (this.mIsRtl ? this.mCurrentPage == getChildCount() - 1 || this.mCurrentPage == getChildCount() - 2 : this.mCurrentPage == 0 || this.mCurrentPage == 1) {
                allAppsPagedCellLayout = null;
            } else if (this.mIsRtl ? this.mCurrentPage == 0 || this.mCurrentPage == 1 : this.mCurrentPage == getChildCount() - 1 || this.mCurrentPage == getChildCount() - 2) {
                allAppsPagedCellLayout2 = null;
            }
            int measuredHeight = (int) ((((getMeasuredHeight() - (getPaddingTop() + getPaddingBottom())) - getScaledMeasuredHeight(getChildAt(0))) / 2) + 0.5f);
            int scaledMeasuredWidth = (((measuredWidth - getScaledMeasuredWidth(getChildAt(0))) / 2) - getChildGap()) - getPageSpacing();
            if (allAppsPagedCellLayout != null && (this.mIsRtl ? this.mDragState == DragState.SCROLL_RIGHT : this.mDragState == DragState.SCROLL_LEFT) && this.mScroller.isFinished()) {
                Resources resources = getResources();
                if (resources == null || (drawable2 = resources.getDrawable(R.drawable.lg_allapps_selected_bg)) == null) {
                    return;
                }
                drawable2.setBounds(getScrollX(), allAppsPagedCellLayout.getTop() + measuredHeight + mCellLayout_scale_translationY, getScrollX() + scaledMeasuredWidth, (allAppsPagedCellLayout.getBottom() - measuredHeight) + mCellLayout_scale_translationY);
                drawable2.draw(canvas);
                return;
            }
            if (allAppsPagedCellLayout2 != null) {
                if (this.mIsRtl) {
                    if (this.mDragState != DragState.SCROLL_LEFT) {
                        return;
                    }
                } else if (this.mDragState != DragState.SCROLL_RIGHT) {
                    return;
                }
                if (!this.mScroller.isFinished() || getResources() == null || (drawable = getResources().getDrawable(R.drawable.lg_allapps_selected_bg)) == null) {
                    return;
                }
                drawable.setBounds((getScrollX() + getWidth()) - scaledMeasuredWidth, allAppsPagedCellLayout2.getTop() + measuredHeight + mCellLayout_scale_translationY, getScrollX() + getWidth(), (allAppsPagedCellLayout2.getBottom() - measuredHeight) + mCellLayout_scale_translationY);
                drawable.draw(canvas);
            }
        }
    }

    private int getScaledMeasuredWidth(View child) {
        if (child == null) {
            return 0;
        }
        return (int) ((child.getMeasuredWidth() * this.mLayoutScale) + 0.5f);
    }

    private int getScaledMeasuredHeight(View child) {
        if (child == null) {
            return 0;
        }
        return (int) ((child.getMeasuredHeight() * this.mLayoutScale) + 0.5f);
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x003b A[PHI: r1 r3
      0x003b: PHI (r1v4 int) = (r1v3 int), (r1v3 int), (r1v9 int), (r1v3 int) binds: [B:10:0x0028, B:16:0x0033, B:18:0x0037, B:14:0x0030] A[DONT_GENERATE, DONT_INLINE]
      0x003b: PHI (r3v2 int) = (r3v1 int), (r3v1 int), (r3v1 int), (r3v4 int) binds: [B:10:0x0028, B:16:0x0033, B:18:0x0037, B:14:0x0030] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void enableHwLayersOnVisiblePages() {
        /*
            r10 = this;
            int r0 = r10.getChildCount()
            r1 = 5
            if (r0 > r1) goto L8
            return
        L8:
            int[] r1 = r10.mTempVisiblePagesRange
            r10.getVisiblePages(r1)
            int[] r1 = r10.mTempVisiblePagesRange
            r2 = 0
            r1 = r1[r2]
            int[] r3 = r10.mTempVisiblePagesRange
            r4 = 1
            r3 = r3[r4]
            android.content.Context r5 = r10.getContext()
            com.lge.launcher3.screeneffect.LoopNormalModeManager r5 = com.lge.launcher3.screeneffect.LoopNormalModeManager.getInstance(r5)
            if (r5 == 0) goto L26
            boolean r5 = r5.isEnabled(r10)
            goto L27
        L26:
            r5 = r2
        L27:
            r6 = -1
            if (r1 != r3) goto L3b
            int r7 = r0 + (-1)
            if (r3 >= r7) goto L33
            int r3 = r3 + 1
            if (r5 == 0) goto L3b
            goto L3c
        L33:
            if (r1 <= 0) goto L3b
            int r1 = r1 + (-1)
            if (r5 == 0) goto L3b
            r7 = r2
            goto L3c
        L3b:
            r7 = r6
        L3c:
            r5 = r2
        L3d:
            if (r5 >= r0) goto L58
            android.view.View r8 = r10.getPageAt(r5)
            com.android.launcher3.CellLayout r8 = (com.android.launcher3.CellLayout) r8
            if (r1 > r5) goto L51
            if (r5 > r3) goto L51
            boolean r9 = r10.shouldDrawChild(r8)
            if (r9 == 0) goto L51
            r9 = r4
            goto L52
        L51:
            r9 = r2
        L52:
            r8.enableHardwareLayer(r9)
            int r5 = r5 + 1
            goto L3d
        L58:
            if (r7 == r6) goto L76
            android.view.View r0 = r10.getPageAt(r7)
            com.android.launcher3.CellLayout r0 = (com.android.launcher3.CellLayout) r0
            com.android.launcher3.ShortcutAndWidgetContainer r1 = r0.getShortcutsAndWidgets()
            if (r1 == 0) goto L76
            int r1 = r1.getLayerType()
            r2 = 2
            if (r1 == r2) goto L76
            boolean r1 = r10.shouldDrawChild(r0)
            if (r1 == 0) goto L76
            r0.enableHardwareLayer(r4)
        L76:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.allapps.AllAppsPagedView.enableHwLayersOnVisiblePages():void");
    }

    private void allowHardwareLayerCreation() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(i);
            if (allAppsPagedCellLayout != null) {
                allAppsPagedCellLayout.enableHardwareLayer(true);
            }
        }
    }

    void destroyHardwareLayerCreation() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(i);
            if (allAppsPagedCellLayout != null) {
                allAppsPagedCellLayout.enableHardwareLayer(false);
            }
        }
    }

    void destroyHardwareLayerCreationExceptCurpage() {
        AllAppsPagedCellLayout allAppsPagedCellLayout;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (this.mCurrentPage != i && (allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(i)) != null) {
                allAppsPagedCellLayout.enableHardwareLayer(false);
            }
        }
    }

    private void dropAnimation(DragView dragView, final View dragSrc) {
        AllAppsItemInfo allAppsItemInfo = (AllAppsItemInfo) dragSrc.getTag();
        int[] iArr = new int[2];
        int[] iArr2 = new int[2];
        if ((!this.mScroller.isFinished() && this.mNextPage != allAppsItemInfo.screenId) || (this.mScroller.isFinished() && this.mCurrentPage != allAppsItemInfo.screenId)) {
            this.mLauncher.getDragLayer().removeView(dragView);
            dragSrc.setVisibility(0);
            return;
        }
        AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt((int) allAppsItemInfo.screenId);
        if (allAppsPagedCellLayout != null) {
            Rect positionWithId = allAppsPagedCellLayout.getPositionWithId((allAppsItemInfo.cellY * this.mCellCountX) + allAppsItemInfo.cellX);
            iArr[0] = positionWithId.left;
            iArr[1] = positionWithId.top;
        }
        getMapPointWithScale(iArr, mCellLayout_middle_scale_xFactor);
        if (allAppsPagedCellLayout != null && this.mScroller.isFinished()) {
            iArr[0] = iArr[0] + (allAppsPagedCellLayout.getLeft() - getScrollX());
            iArr[1] = iArr[1] + (allAppsPagedCellLayout.getTop() - getScrollY());
        } else {
            iArr[1] = iArr[1] + getPaddingTop();
        }
        getLocationOnScreen(iArr2);
        iArr[0] = iArr[0] + (iArr2[0] - 1);
        iArr[1] = iArr[1] + (iArr2[1] - 1);
        this.mLauncher.getDragLayer().getLocationOnScreen(iArr2);
        iArr[0] = iArr[0] - (iArr2[0] - 1);
        iArr[1] = iArr[1] - (iArr2[1] - 1);
        if (dragSrc instanceof TextView) {
            Drawable drawable = ((TextView) dragSrc).getCompoundDrawablesRelative()[(this.mLauncher.getDeviceProfile().isLandscape && this.mLauncher.getDeviceProfile().allowRotation && !LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) ? (char) 0 : (char) 1];
            iArr[1] = iArr[1] + Math.round(mCellLayout_middle_scale_xFactor * dragSrc.getPaddingTop());
            iArr[1] = iArr[1] - ((dragView.getMeasuredHeight() - Math.round(mCellLayout_middle_scale_xFactor * drawable.getIntrinsicHeight())) / 2);
            iArr[0] = iArr[0] - ((dragView.getMeasuredWidth() - Math.round(mCellLayout_middle_scale_xFactor * dragSrc.getMeasuredWidth())) / 2);
        } else if (dragSrc instanceof FolderIcon) {
            iArr[1] = iArr[1] - ((dragView.getMeasuredHeight() - Math.round(mCellLayout_middle_scale_xFactor * dragSrc.getMeasuredHeight())) / 2);
            iArr[0] = iArr[0] - ((dragView.getMeasuredWidth() - Math.round(mCellLayout_middle_scale_xFactor * dragSrc.getMeasuredWidth())) / 2);
        }
        Runnable runnable = new Runnable() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.12
            @Override // java.lang.Runnable
            public void run() {
                AllAppsPagedView.this.mLauncher.getDragLayer().clearAnimatedView();
                dragSrc.setVisibility(0);
                View view = dragSrc;
                if (view instanceof BubbleTextView) {
                    ((BubbleTextView) view).setItemInfo();
                }
            }
        };
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        int translationX = (int) dragView.getTranslationX();
        int translationY = (int) dragView.getTranslationY();
        int i = iArr[0];
        int i2 = iArr[1];
        float f = mCellLayout_middle_scale_xFactor;
        dragLayer.animateViewIntoPosition(dragView, translationX, translationY, i, i2, 1.0f, 1.0f, 1.0f, f, f, runnable, 2, -1, null);
    }

    @Override // com.lge.launcher3.PagedView, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }
        int viewportOffsetX = getViewportOffsetX();
        this.mViewport.offset(viewportOffsetX, getViewportOffsetY());
        int i = this.mIsRtl ? -1 : childCount;
        int i2 = this.mIsRtl ? -1 : 1;
        int paddingTop = getPaddingTop() + getPaddingBottom();
        int paddingStart = getPaddingStart() + viewportOffsetX;
        if (this.mPageScrolls == null || childCount != this.mChildCountOnLastLayout) {
            this.mPageScrolls = new int[childCount];
        }
        if (this.mFirstLayout && this.mCurrentPage >= 0 && this.mCurrentPage < getChildCount()) {
            setHorizontalScrollBarEnabled(false);
            int childOffset = getChildOffset(this.mCurrentPage) - getRelativeChildOffset(this.mCurrentPage);
            scrollTo(childOffset, 0);
            this.mScroller.setFinalX(childOffset);
            setHorizontalScrollBarEnabled(true);
            this.mFirstLayout = false;
        }
        if (childCount > 0) {
            paddingStart = getRelativeChildOffset(0);
        }
        for (int i3 = this.mIsRtl ? childCount - 1 : 0; i3 != i; i3 += i2) {
            View childAt = getChildAt(i3);
            if (childAt != null && childAt.getVisibility() != 8) {
                int scaledMeasuredWidth = getScaledMeasuredWidth(childAt);
                int measuredHeight = childAt.getMeasuredHeight();
                int measuredHeight2 = this.mPaddingTop;
                if (this.mCenterPagesVertically) {
                    measuredHeight2 += ((getMeasuredHeight() - paddingTop) - measuredHeight) / 2;
                }
                childAt.layout(paddingStart, measuredHeight2, childAt.getMeasuredWidth() + paddingStart, measuredHeight + measuredHeight2);
                this.mPageScrolls[i3] = (paddingStart - getPaddingStart()) - viewportOffsetX;
                paddingStart += scaledMeasuredWidth + getPageSpacing() + getChildGap();
            }
        }
        updatePageScrollsForLoop();
        LayoutTransition layoutTransition = getLayoutTransition();
        if (layoutTransition != null && layoutTransition.isRunning()) {
            layoutTransition.addTransitionListener(new LayoutTransition.TransitionListener() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.13
                @Override // android.animation.LayoutTransition.TransitionListener
                public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                }

                @Override // android.animation.LayoutTransition.TransitionListener
                public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                    if (transition.isRunning()) {
                        return;
                    }
                    transition.removeTransitionListener(this);
                    AllAppsPagedView.this.updateMaxScrollX();
                }
            });
        } else {
            updateMaxScrollX();
        }
        updateCurrentPageScroll();
        if (this.mFirstLayout && this.mCurrentPage >= 0 && this.mCurrentPage < childCount) {
            this.mFirstLayout = false;
        }
        if (this.mScroller.isFinished() && this.mChildCountOnLastLayout != childCount) {
            if (this.mRestorePage != -1001) {
                setCurrentPage(this.mRestorePage);
                this.mRestorePage = PagedView.INVALID_RESTORE_PAGE;
            } else {
                setCurrentPage(getNextPage());
            }
        }
        this.mChildCountOnLastLayout = childCount;
    }

    @Override // com.lge.launcher3.PagedView, com.android.launcher3.DragScroller
    public void scrollLeft() {
        super.scrollLeft();
        Folder openFolder = getOpenFolder();
        if (openFolder != null) {
            openFolder.completeDragExit();
        }
    }

    @Override // com.lge.launcher3.PagedView, com.android.launcher3.DragScroller
    public void scrollRight() {
        super.scrollRight();
        Folder openFolder = getOpenFolder();
        if (openFolder != null) {
            openFolder.completeDragExit();
        }
    }

    private Bitmap getIconBitmap(AllAppsItemInfo itemInfo) {
        if (itemInfo == null) {
            LGLog.w(TAG, "itemInfo == null", new int[0]);
            return null;
        }
        itemInfo.bScaled = false;
        return itemInfo.iconBitmap;
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0095  */
    /* JADX WARN: Removed duplicated region for block: B:30:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:26:0x00b7 -> B:22:0x0093). Please report as a decompilation issue!!! */
    @Override // com.lge.launcher3.PagedView
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void screenScrolled(int r7) {
        /*
            r6 = this;
            boolean r0 = r6.isHardwareAccelerated()
            if (r0 == 0) goto L9
            r6.enableHwLayersOnVisiblePages()
        L9:
            boolean r0 = r6.isScaleAniRunning()
            if (r0 != 0) goto Lba
            super.screenScrolled(r7)
            boolean r0 = r6.isInArrangeMode()
            if (r0 == 0) goto Lba
            int r0 = r6.mCurrentPage
            android.view.View r0 = r6.getChildAt(r0)
            com.lge.launcher3.allapps.AllAppsPagedCellLayout r0 = (com.lge.launcher3.allapps.AllAppsPagedCellLayout) r0
            if (r0 == 0) goto Lba
            int r1 = r6.getChildCount()
            int r2 = r6.mCurrentPage
            int r2 = r2 + 1
            if (r1 == r2) goto L33
            int r1 = r0.getId()
            r2 = -1
            if (r1 != r2) goto L38
        L33:
            int r1 = r6.mCurrentPage
            r0.setId(r1)
        L38:
            int r1 = r6.mCurrentPage
            int r2 = r0.getId()
            int r2 = r2 + (-1)
            r3 = 0
            if (r1 != r2) goto L67
            int r1 = r6.getChildCount()
            int r1 = r1 + (-1)
            int r2 = r0.getId()
            int r2 = r2 + 1
            int r2 = r2 + (-1)
            int r1 = java.lang.Math.min(r1, r2)
            int r2 = r0.getId()
            int r2 = r2 + (-1)
            int r2 = r2 + (-1)
            int r2 = java.lang.Math.max(r3, r2)
            int r3 = r6.mCurrentPage
            r0.setId(r3)
            goto L93
        L67:
            int r1 = r6.getChildCount()
            int r1 = r1 + (-1)
            int r2 = r0.getId()
            int r2 = r2 + 1
            int r1 = java.lang.Math.min(r1, r2)
            int r2 = r0.getId()
            int r2 = r2 + (-1)
            int r2 = java.lang.Math.max(r3, r2)
            int r3 = r6.mCurrentPage
            int r4 = r0.getId()
            int r4 = r4 + 1
            if (r3 != r4) goto L93
            int r3 = r6.mCurrentPage
            r0.setId(r3)
            int r1 = r1 + 1
            goto Lb7
        L93:
            if (r2 > r1) goto Lba
            android.view.View r0 = r6.getPageAt(r2)
            if (r0 == 0) goto Lb7
            int r3 = r6.indexOfChild(r0)
            float r3 = r6.getScrollProgress(r7, r0, r3)
            float r3 = java.lang.Math.abs(r3)
            r4 = 1065353216(0x3f800000, float:1.0)
            float r4 = r4 - r3
            float r5 = com.lge.launcher3.allapps.AllAppsPagedView.mCellLayout_middle_scale_xFactor
            float r4 = r4 * r5
            float r5 = com.lge.launcher3.allapps.AllAppsPagedView.mCellLayout_scale_xFactor
            float r3 = r3 * r5
            float r4 = r4 + r3
            r0.setScaleX(r4)
            r0.setScaleY(r4)
        Lb7:
            int r2 = r2 + 1
            goto L93
        Lba:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.allapps.AllAppsPagedView.screenScrolled(int):void");
    }

    @Override // com.lge.launcher3.PagedView
    protected void onPageBeginTransition() {
        super.onPageBeginTransition();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (this.scaleAni != null) {
            LGLog.d(TAG, "scaleAni Not Null, can't precess dispatchTouchEvent");
            return true;
        }
        return super.dispatchTouchEvent(event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setClipChildrenAtPage(int page, boolean bEnable) {
        AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(page);
        if (allAppsPagedCellLayout != null) {
            allAppsPagedCellLayout.setClipChildren(bEnable);
            allAppsPagedCellLayout.setClipToPadding(bEnable);
            allAppsPagedCellLayout.getShortcutsAndWidgets().setClipChildren(bEnable);
            allAppsPagedCellLayout.getShortcutsAndWidgets().setClipToPadding(bEnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void rearrangingItems() {
        int i = this.mswapIndex;
        AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(this.mCurrentPage);
        if (allAppsPagedCellLayout != null && this.mScroller.isFinished()) {
            cleanupFolderCreation();
            int i2 = this.mLastFolderAniIdx;
            if (i2 != -1 && (i2 | 512) != i) {
                View childOnPageId = allAppsPagedCellLayout.getChildOnPageId(i2);
                View childOnPageId2 = allAppsPagedCellLayout.getChildOnPageId(this.mdragIndex);
                if (childOnPageId != null && childOnPageId2 != null) {
                    AllAppsItemInfo allAppsItemInfo = (AllAppsItemInfo) childOnPageId.getTag();
                    AllAppsItemInfo allAppsItemInfo2 = (AllAppsItemInfo) childOnPageId2.getTag();
                    if (allAppsItemInfo.itemType == 0) {
                        cancelMakeFolder(true);
                    } else {
                        ((FolderIcon) childOnPageId).onDragExit(allAppsItemInfo2);
                    }
                }
                this.mLastFolderAniIdx = -1;
            }
            int i3 = this.mdragIndex;
            int i4 = i & 255;
            if (i3 != i4 && i != -1 && i3 != -1 && (i & 65280) == 256) {
                if (this.mdragPage != this.mCurrentPage) {
                    return;
                }
                if (allAppsPagedCellLayout.getChildOnPageId(i4) == null) {
                    int pageChildCount = allAppsPagedCellLayout.getPageChildCount() - 1;
                    if (pageChildCount < i4) {
                        if (allAppsPagedCellLayout.getChildOnPageId(pageChildCount) != null) {
                            allAppsPagedCellLayout.swapViewOnPageAt(this.mdragIndex, pageChildCount);
                            if (this.mdragIndex != pageChildCount) {
                                VibratorManager.performHapticFeedback(this.mLauncher, 65541);
                            }
                            this.mdragIndex = pageChildCount;
                            this.mswapIndex = -1;
                            return;
                        }
                        return;
                    }
                    LGLog.d("[PageMenu]", "Drag Over currentIdx is null");
                    return;
                }
                allAppsPagedCellLayout.swapViewOnPageAt(this.mdragIndex, i4);
                this.mdragIndex = i4;
                this.mswapIndex = -1;
                talkbackReadCurrentPosition(false);
                VibratorManager.performHapticFeedback(this.mLauncher, 65541);
                return;
            }
            if (i3 == i4 || i == -1 || i3 == -1 || (i & 65280) != 512) {
                return;
            }
            View childOnPageId3 = allAppsPagedCellLayout.getChildOnPageId(i4);
            View childOnPageId4 = allAppsPagedCellLayout.getChildOnPageId(this.mdragIndex);
            if (childOnPageId3 != null && !(childOnPageId3 instanceof FolderIcon) && childOnPageId4 != null) {
                AllAppsItemInfo allAppsItemInfo3 = (AllAppsItemInfo) childOnPageId3.getTag();
                AllAppsItemInfo allAppsItemInfo4 = (AllAppsItemInfo) childOnPageId4.getTag();
                if (allAppsItemInfo3 == null || allAppsItemInfo3.itemType != 0 || allAppsItemInfo4.itemType != 0 || i4 == this.mLastFolderAniIdx) {
                    return;
                }
                if (this.mDragController.getDragView() == null) {
                    LGLog.d("[PageMenu]", "mDragController.getDragView() == null");
                    return;
                }
                getResources().getInteger(R.integer.lg_default_folder_color_index);
                this.mFolderCreationAlarm.setOnAlarmListener(new AllAppsFolderCreationAlarmListener(allAppsPagedCellLayout, allAppsItemInfo3.cellX, allAppsItemInfo3.cellY));
                this.mFolderCreationAlarm.setAlarm(0L);
                this.mLastFolderAniIdx = i4;
                if (TalkBackUtils.isEnabled(getContext())) {
                    announceForAccessibility(getResources().getString(R.string.folder_name));
                    return;
                }
                return;
            }
            if (!(childOnPageId3 instanceof FolderIcon) || childOnPageId4 == null || i4 == this.mLastFolderAniIdx) {
                return;
            }
            FolderIcon folderIcon = (FolderIcon) childOnPageId3;
            ItemInfo itemInfo = (ItemInfo) childOnPageId4.getTag();
            if (folderIcon.acceptDrop(itemInfo)) {
                folderIcon.onDragEnter(itemInfo);
                this.mLastFolderAniIdx = i4;
            }
            if (TalkBackUtils.isEnabled(getContext())) {
                announceForAccessibility(folderIcon.getFolderInfo().title.toString() + "," + getResources().getString(R.string.folder_name));
            }
        }
    }

    class AllAppsFolderCreationAlarmListener implements OnAlarmListener {
        int cellX;
        int cellY;
        CellLayout layout;

        public AllAppsFolderCreationAlarmListener(CellLayout layout, int cellX, int cellY) {
            this.layout = layout;
            this.cellX = cellX;
            this.cellY = cellY;
        }

        @Override // com.android.launcher3.OnAlarmListener
        public void onAlarm(Alarm alarm) {
            if (AllAppsPagedView.this.mDragFolderRingAnimator != null) {
                AllAppsPagedView.this.mDragFolderRingAnimator.animateToNaturalState();
            }
            AllAppsPagedView.this.mDragFolderRingAnimator = new FolderIcon.FolderRingAnimator(AllAppsPagedView.this.mLauncher, null);
            AllAppsPagedView.this.mDragFolderRingAnimator.setCell(this.cellX, this.cellY);
            AllAppsPagedView.this.mDragFolderRingAnimator.setCellLayout(this.layout);
            AllAppsPagedView.this.mDragFolderRingAnimator.animateToAcceptState();
            this.layout.showFolderAccept(AllAppsPagedView.this.mDragFolderRingAnimator);
            this.layout.clearDragOutlines();
            TalkBackUtils.sendAccessibilityEvent((Context) AllAppsPagedView.this.mLauncher, AllAppsPagedView.this.getResources().getString(R.string.folder_name), true);
        }
    }

    private void cleanupFolderCreation() {
        FolderIcon.FolderRingAnimator folderRingAnimator = this.mDragFolderRingAnimator;
        if (folderRingAnimator != null) {
            folderRingAnimator.animateToNaturalState();
            this.mDragFolderRingAnimator = null;
        }
        this.mFolderCreationAlarm.setOnAlarmListener(null);
        this.mFolderCreationAlarm.cancelAlarm();
    }

    private float[] getDragViewVisualCenter(int x, int y, int xOffset, int yOffset, DragView dragView, float[] recycle) {
        if (recycle == null) {
            recycle = new float[2];
        }
        recycle[0] = (x - xOffset) + (dragView.getDragRegion().width() / 2);
        recycle[1] = (y - yOffset) + (dragView.getDragRegion().height() / 2);
        return recycle;
    }

    void onDestroy() {
        unbindFolder();
        endArrangeMode(true);
        removeCallbacks(this.mRunnable);
        setHostListener(null);
        removeAllAppsView();
        this.mSearchUtil.onDestroy();
    }

    private void removeAllAppsView() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(i);
            if (allAppsPagedCellLayout != null) {
                allAppsPagedCellLayout.removeAllViewsOnPage();
            }
        }
        removeAllViews();
    }

    @Override // com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragStart(DragSource source, Object info, int dragAction) {
        cleanupFolderCreation();
    }

    @Override // com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragEnd() {
        clearAllHovers();
        this.mItemFromFolder = null;
        this.mDragItemFromFolder = null;
        if (getOpenFolder() == null && TalkBackUtils.isEnabled(getContext())) {
            announceForAccessibility(getResources().getString(R.string.sp_moved_NORMAL));
        }
    }

    void postHardwareLayerOn() {
        if (this.mIsAllAppsLoaded) {
            post(new Runnable() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.15
                @Override // java.lang.Runnable
                public void run() {
                    AllAppsPagedView.this.setHardwareLayer(true);
                }
            });
        }
    }

    void setHardwareLayer(boolean bHardwareLayer) {
        int childCount = getChildCount();
        if (childCount <= 5) {
            if (bHardwareLayer) {
                allowHardwareLayerCreation();
                return;
            } else {
                destroyHardwareLayerCreation();
                return;
            }
        }
        View childAt = getChildAt(this.mCurrentPage);
        int id = (childAt != null ? childAt.getId() : -1) - 2;
        if (id < 0) {
            id += childCount;
        }
        int i = 0;
        for (int i2 = 0; i2 < 5; i2++) {
            int i3 = i2 + id;
            AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getPageAt(i3 + i);
            if (allAppsPagedCellLayout != null) {
                allAppsPagedCellLayout.enableHardwareLayer(bHardwareLayer);
                if (bHardwareLayer) {
                    allAppsPagedCellLayout.buildLayer();
                }
            }
            if (i3 == childCount - 1) {
                i = -childCount;
            }
        }
    }

    private boolean isScaleAniRunning() {
        ValueAnimator valueAnimator = this.scaleAni;
        return valueAnimator != null && valueAnimator.isRunning();
    }

    private boolean isInCellLayout(int x, int y, int page) {
        AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(page);
        if (allAppsPagedCellLayout != null) {
            if (this.mIsRtl) {
                x = ((int) (allAppsPagedCellLayout.getWidth() * mCellLayout_middle_scale_xFactor)) + (this.mMaxScroll - x);
            }
            int[] iArr = new int[2];
            int[] iArr2 = new int[2];
            ViewPosition.getLocationInDragLayer(this.mLauncher.getDragLayer(), allAppsPagedCellLayout, iArr);
            ViewPosition.getLocationInDragLayer(this.mLauncher.getDragLayer(), this, iArr2);
            iArr[1] = iArr[1] - iArr2[1];
            if (iArr[0] < x && x < iArr[0] + (((int) (allAppsPagedCellLayout.getWidth() * mCellLayout_middle_scale_xFactor)) * (page + 1)) && iArr[1] < y && y < iArr[1] + ((int) (allAppsPagedCellLayout.getHeight() * mCellLayout_middle_scale_xFactor))) {
                return true;
            }
        }
        return false;
    }

    protected boolean determineScrollLeft(View current_child, View final_child) {
        if (!isInArrangeMode() || getChildCount() <= 2) {
            return final_child.getId() > current_child.getId();
        }
        if (final_child.getId() == 0) {
            return true;
        }
        LGLog.d(TAG, "determineScrollLeft : false");
        return false;
    }

    protected boolean determineScrollRight(View current_child, View final_child) {
        if (!isInArrangeMode() || getChildCount() <= 2) {
            return final_child.getId() < current_child.getId();
        }
        if (final_child.getId() == getChildCount() - 1) {
            return true;
        }
        LGLog.d(TAG, "determineScrollLeft : false");
        return false;
    }

    void resetNextPage() {
        this.mNextPage = -1;
        setPageMoving(false);
    }

    public void setCellCountFromPreference() {
        int[] layoutNumFromPreference = AllAppsUtils.getLayoutNumFromPreference(this.mContext, this.mMenuItemFactory.getCellCount());
        String str = TAG;
        LGLog.d(str, "mCellCountXInPreference cellcount[0] = " + layoutNumFromPreference[0] + " cellcount[1] = " + layoutNumFromPreference[1]);
        LGLog.d(str, "mCellCountXInPreference mCellCountX = " + this.mCellCountX + " mCellCountY = " + this.mCellCountY);
        if (layoutNumFromPreference[0] == this.mCellCountX && layoutNumFromPreference[1] == this.mCellCountY) {
            return;
        }
        this.mCellCountX = layoutNumFromPreference[0];
        this.mCellCountY = layoutNumFromPreference[1];
        this.mMenuItemFactory.setCellCountXY(layoutNumFromPreference[0], layoutNumFromPreference[1], makeMenuItems());
    }

    private void removeVacantPageOnSearch() {
        int childCount = getChildCount();
        int i = 0;
        while (i < childCount) {
            AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(i);
            if (allAppsPagedCellLayout != null && allAppsPagedCellLayout.getPageChildCount() == 0) {
                removeView(allAppsPagedCellLayout);
                i--;
            }
            i++;
        }
    }

    private void loadAllAppsListForSearch(ArrayList<AllAppsItemInfo> menuItemInfos, boolean convert) {
        if (menuItemInfos != null) {
            resetAllAppsPageData();
            addAppsViewForSearch(menuItemInfos);
            View.OnLayoutChangeListener onLayoutChangeListener = this.mLayoutListener;
            if (onLayoutChangeListener != null) {
                removeOnLayoutChangeListener(onLayoutChangeListener);
            }
            View.OnLayoutChangeListener onLayoutChangeListener2 = new View.OnLayoutChangeListener() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.16
                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    AllAppsPagedView.this.setCurrentPageAftterSearchLoading();
                    v.removeOnLayoutChangeListener(AllAppsPagedView.this.mLayoutListener);
                    AllAppsPagedView.this.mLayoutListener = null;
                }
            };
            this.mLayoutListener = onLayoutChangeListener2;
            addOnLayoutChangeListener(onLayoutChangeListener2);
            requestLayout();
            setHardwareLayer(true);
        }
        if (convert) {
            removeVacantPageOnSearch();
        }
        this.mIsAllAppsLoaded = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCurrentPageAftterSearchLoading() {
        int i = this.mSearchSavedItemIndex;
        if (i >= 0) {
            setCurrentPage(i);
            this.mSearchSavedItemIndex = -1;
            this.mSaveInstanceStateItemIndex = -1;
            return;
        }
        setCurrentPage(0);
    }

    private void addAppsViewForSearch(ArrayList<AllAppsItemInfo> menuItemInfos) {
        for (AllAppsItemInfo allAppsItemInfo : menuItemInfos) {
            if (allAppsItemInfo != null) {
                int i = (int) allAppsItemInfo.screenId;
                int i2 = (allAppsItemInfo.cellY * this.mCellCountX) + allAppsItemInfo.cellX;
                AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(i);
                if (allAppsPagedCellLayout != null) {
                    if (allAppsItemInfo.itemType == 0) {
                        if (allAppsItemInfo.itemView == null) {
                            this.mTextViewPool.createMenuTextView(allAppsItemInfo, getIconBitmap(allAppsItemInfo));
                        }
                        allAppsPagedCellLayout.addViewToCellLayout(allAppsItemInfo.itemView, -1, i2, new AllAppsPagedCellLayoutParam(allAppsItemInfo.cellX, allAppsItemInfo.cellY, 1, 1));
                    } else {
                        AllAppsFolderInfo allAppsFolderInfo = allAppsItemInfo.mFolderInfo;
                        allAppsItemInfo.itemView = createMenuFolderIcon(allAppsPagedCellLayout, allAppsFolderInfo, allAppsFolderInfo.folderColor);
                        FolderIcon folderIcon = (FolderIcon) allAppsItemInfo.itemView;
                        folderIcon.setTag(allAppsItemInfo);
                        UninstallModeManager.getInstance(getContext()).setUninstallTypeForItemsInFolder((FolderPagedView) folderIcon.getFolder().getContent());
                        allAppsPagedCellLayout.addViewToCellLayout(allAppsItemInfo.itemView, -1, i2, new AllAppsPagedCellLayoutParam(allAppsItemInfo.cellX, allAppsItemInfo.cellY, 1, 1));
                    }
                    allAppsItemInfo.itemView.setTranslationX(0.0f);
                    allAppsItemInfo.itemView.setTranslationY(0.0f);
                }
            }
        }
    }

    private void setSpannable(ArrayList<AllAppsItemInfo> menuItemInfos) {
        BubbleTextView bubbleTextView;
        for (AllAppsItemInfo allAppsItemInfo : menuItemInfos) {
            if (allAppsItemInfo.itemType == 0 && (bubbleTextView = (BubbleTextView) allAppsItemInfo.itemView) != null) {
                bubbleTextView.highlightSearchText(allAppsItemInfo);
            }
        }
    }

    private void clearSpannable(ArrayList<AllAppsItemInfo> menuItemInfos) {
        for (AllAppsItemInfo allAppsItemInfo : menuItemInfos) {
            View view = allAppsItemInfo.itemView;
            if (view != null) {
                view.setTranslationX(0.0f);
                view.setTranslationY(0.0f);
                view.setTag(allAppsItemInfo);
                if (view instanceof BubbleTextView) {
                    ((BubbleTextView) view).clearHighlight(allAppsItemInfo);
                }
            }
        }
    }

    @Override // com.lge.launcher3.allapps.AllAppsView
    public void addApps(ArrayList<AppInfo> addList, int op) {
        addApps(addList, op, false);
    }

    private void addApps(ArrayList<AppInfo> addList, int op, boolean immediately) {
        if (addList == null || addList.size() == 0 || !this.mIsAllAppsLoaded) {
            return;
        }
        boolean zIsPaused = this.mLauncher.isPaused();
        if (isMenuStopState()) {
            String str = TAG;
            LGLog.d(str, " Launcher is paused = " + zIsPaused);
            LGLog.i(str, "addApps addList = " + addList);
            this.mAppUtil.appBindingCompress(addList, op);
            return;
        }
        this.mAppUtil.addApps(addList);
        int i = -1;
        ArrayList<AllAppsItemInfo> arrayList = new ArrayList<>();
        boolean z = addList.size() <= 50;
        Iterator<AppInfo> it = addList.iterator();
        while (it.hasNext()) {
            AllAppsItemInfo allAppsItemInfoAddNewApplicationToLastPage = addNewApplicationToLastPage(it.next(), z, false);
            if (allAppsItemInfoAddNewApplicationToLastPage != null) {
                i = (int) allAppsItemInfoAddNewApplicationToLastPage.screenId;
            }
            if (!z) {
                arrayList.add(allAppsItemInfoAddNewApplicationToLastPage);
            }
            if (getCurrentPage() != i) {
                View.OnLayoutChangeListener onLayoutChangeListener = this.mLayoutListener;
                if (onLayoutChangeListener != null) {
                    removeOnLayoutChangeListener(onLayoutChangeListener);
                }
                View.OnLayoutChangeListener onLayoutChangeListener2 = new View.OnLayoutChangeListener() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.17
                    @Override // android.view.View.OnLayoutChangeListener
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        v.removeOnLayoutChangeListener(AllAppsPagedView.this.mLayoutListener);
                        AllAppsPagedView.this.mLayoutListener = null;
                    }
                };
                this.mLayoutListener = onLayoutChangeListener2;
                addOnLayoutChangeListener(onLayoutChangeListener2);
            }
        }
        if (arrayList.size() > 0) {
            this.mMenuItemFactory.bulkInsertItems(arrayList);
        }
        requestLayout();
    }

    @Override // com.lge.launcher3.allapps.AllAppsView
    public boolean isAnimating() {
        return getAnimation() != null;
    }

    @Override // com.lge.launcher3.allapps.AllAppsView
    public boolean isVisible() {
        return getVisibility() == 0;
    }

    @Override // com.lge.launcher3.allapps.AllAppsView
    public void removeApps(ArrayList<AppInfo> list) {
        removeApps(list, true);
    }

    private void removeApps(ArrayList<AppInfo> list, boolean immediately) {
        if (list == null || list.size() <= 0 || !this.mIsAllAppsLoaded) {
            return;
        }
        if (isMenuStopState()) {
            LGLog.i(TAG, "removeApps list size= " + list.size());
            this.mAppUtil.appBindingCompress(list, AllAppsConstant.AppState.REMOVE);
            return;
        }
        int i = 0;
        if (list.size() >= 2) {
            immediately = true;
        }
        ArrayList<AppInfo> arrayList = new ArrayList<>();
        for (AppInfo appInfo : list) {
            if (removeApplication(appInfo, immediately)) {
                i++;
            } else {
                arrayList.add(appInfo);
            }
        }
        if (arrayList.size() > 0) {
            this.mAppUtil.appBindingCompress(arrayList, AllAppsConstant.AppState.REMOVE);
        }
        this.mAppUtil.removeAppsWithoutInvalidate(list);
        if (i == 0) {
            return;
        }
        if (!isInArrangeMode()) {
            removeVacantPage();
        }
        this.mMenuItemFactory.updatePositionChangedItems();
    }

    @Override // com.lge.launcher3.allapps.AllAppsView
    public void reset() {
        resetPageMenu();
    }

    void appLoadStart(Boolean immediately) {
        if (this.mAppUtil.isAllAppsEmpty()) {
            if (isVisible() && this.mLauncher.getModel().isAllAppsLoaded()) {
                setApps(this.mLauncher.mModel.getAllAppsList());
                return;
            }
            return;
        }
        if (this.mAppUtil.getRemainBindAppsSize() > 0 || this.mCurrentApps != null) {
            this.mHandler.sendEmptyMessage(5);
        }
    }

    void appUpdateStart() {
        int remainBindAppsSize = this.mAppUtil.getRemainBindAppsSize();
        if (remainBindAppsSize > 0 || this.mCurrentApps != null) {
            if (getVisibility() == 0) {
                if (remainBindAppsSize <= 0 && this.mCurrentApps != null) {
                    int loadFromDbStatus = this.mMenuItemFactory.getLoadFromDbStatus();
                    Objects.requireNonNull(this.mMenuItemFactory);
                    if (loadFromDbStatus == 0) {
                        processReloadState();
                        return;
                    }
                }
                this.mHandler.sendEmptyMessage(5);
                return;
            }
            updateAppList();
            return;
        }
        LGLog.d(TAG, "appUpdateStart no need");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAppList() {
        if (this.mAppUtil.getRemainBindAppsSize() > 0 && !isMenuStopState()) {
            addApps(this.mAppUtil.getRemainBindApps(AllAppsConstant.AppState.ADD), 0, true);
            updateApps(this.mAppUtil.getRemainBindApps(AllAppsConstant.AppState.UPDATE));
            removeApps(this.mAppUtil.getRemainBindApps(AllAppsConstant.AppState.REMOVE), true);
            this.mAppUtil.initBindApps();
        }
    }

    private void setAppsForSearch(ArrayList<AppInfo> list) {
        this.mAppUtil.initBindApps();
        if (isVisible() || this.mIsAllAppsLoaded) {
            LGLog.d(TAG, "setAppsForSearch =" + list.size());
            if (list.size() != 0 && isChangedApps(this.mAppUtil.setApps(list))) {
                searchAppsDirect();
            }
        }
    }

    @Override // com.lge.launcher3.allapps.AllAppsView
    public void setApps(ArrayList<AppInfo> list) {
        String str = TAG;
        LGLog.d(str, "start setApps mIsShowSearchBar:" + this.mIsShowSearchBar + " isVisible:" + isVisible() + " mIsAllAppsLoaded:" + this.mIsAllAppsLoaded);
        initValue();
        setPageSpacing((int) this.mDefault_Spacing);
        setPageGrid();
        if (this.mIsShowSearchBar) {
            setAppsForSearch(list);
            return;
        }
        this.mAppUtil.initBindApps();
        if (isVisible() || this.mIsAllAppsLoaded) {
            LGLog.d(str, "setApps =" + list.size());
            if (this.mLauncher.isPaused()) {
                LGLog.i(str, "setApps mLauncher.isPaused() = " + this.mLauncher.isPaused());
                setReloadState(list, false);
                return;
            }
            if (isVisible() && isInArrangeMode()) {
                this.mDragController.cancelDrag();
            }
            updateAppList();
            if (isChangedApps(this.mAppUtil.setApps(list))) {
                postDelayed(new Runnable() { // from class: com.lge.launcher3.allapps.-$$Lambda$AllAppsPagedView$I-i24tjmu6AqBolnxSWVeUT8HSI
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$setApps$0$AllAppsPagedView();
                    }
                }, 100L);
                UninstallModeManager.getInstance(this.mContext).setUninstallTypeForAllBadgeViews(this);
                this.mIsPortrait = OrientationUtils.isPortrait(getContext());
                this.inverseMatrix1 = null;
                this.inverseMatrix2 = null;
            }
        }
    }

    public /* synthetic */ void lambda$setApps$0$AllAppsPagedView() {
        loadAllAppsList();
        UninstallModeManager.getInstance(this.mContext).setUninstallTypeForAllBadgeViews(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetPageMenu() {
        removeAllFolders();
        this.mMenuItemFactory.resetDatabase(this.mAppUtil.getApps(true));
        loadAllAppsList();
        UninstallModeManager.getInstance(this.mContext).setUninstallTypeForAllBadgeViews(this);
        invalidate();
        setCurrentPage(0);
    }

    private void removeAllFolders() {
        ArrayList<AllAppsItemInfo> allAppsItemInfoList = this.mMenuItemFactory.getAllAppsItemInfoList();
        for (int size = allAppsItemInfoList.size() - 1; size >= 0; size--) {
            AllAppsItemInfo allAppsItemInfo = allAppsItemInfoList.get(size);
            if (allAppsItemInfo != null && allAppsItemInfo.itemType == 2) {
                this.mMenuItemFactory.removeFolder(allAppsItemInfo);
            }
        }
    }

    private void unbindFolder() {
        AllAppsFolderInfo allAppsFolderInfo;
        for (AllAppsItemInfo allAppsItemInfo : this.mMenuItemFactory.getAllAppsItemInfoList()) {
            if (allAppsItemInfo != null && allAppsItemInfo.itemType == 2 && (allAppsFolderInfo = allAppsItemInfo.mFolderInfo) != null) {
                allAppsFolderInfo.unbind();
            }
        }
    }

    @Override // com.lge.launcher3.allapps.AllAppsView
    public void updateApps(ArrayList<AppInfo> updateList) {
        if (updateList == null || updateList.size() <= 0 || !this.mIsAllAppsLoaded) {
            return;
        }
        if (isMenuStopState()) {
            LGLog.i(TAG, "updateApps updateList size = " + updateList.size());
            this.mAppUtil.appBindingCompress(updateList, AllAppsConstant.AppState.UPDATE);
            return;
        }
        this.mAppUtil.removeAppsWithoutInvalidate(updateList);
        this.mAppUtil.addApps(updateList);
        Iterator<AppInfo> it = updateList.iterator();
        while (it.hasNext()) {
            updateApplication(it.next());
        }
    }

    private void updateApplication(AppInfo appInfo) {
        AllAppsItemInfo allAppsItemInfoUpdateMenuItemInfo = this.mMenuItemFactory.updateMenuItemInfo(appInfo);
        if (allAppsItemInfoUpdateMenuItemInfo != null) {
            BubbleTextView bubbleTextView = (BubbleTextView) allAppsItemInfoUpdateMenuItemInfo.itemView;
            if (bubbleTextView != null) {
                bubbleTextView.applyFromApplicationInfo(allAppsItemInfoUpdateMenuItemInfo);
                bubbleTextView.getTag();
                bubbleTextView.invalidate();
            }
            allAppsItemInfoUpdateMenuItemInfo.mLowerTitle = null;
            return;
        }
        updateFolderItemInfo(appInfo);
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent ev) {
        int action = ev.getAction();
        if (action != 0 && action != 2) {
            return false;
        }
        this.mIconLastTouchPos.set((int) ev.getX(), (int) ev.getY());
        return false;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (getVisibility() != 0) {
            LGLog.w(TAG, "onClick: Do nothing because the view is invisible! " + v, new int[0]);
            return;
        }
        String str = TAG;
        LGLog.d(str, "onClick. View=" + v);
        ItemInfo itemInfo = (ItemInfo) v.getTag();
        if (itemInfo instanceof AllAppsItemInfo) {
            if (itemInfo.itemType == 0) {
                if (isInArrangeMode()) {
                    LGLog.d(str, "onClick. mArrangeMode = true");
                    if (UninstallModeManager.getInstance(this.mContext).checkAndShowUninstallPopup(this.mLauncher, v)) {
                        return;
                    }
                }
                LGLog.d(str, "onClick. mArrangeMode == false");
                CPUBoostService.boostUp(this.mContext);
                LGLog.d(str, "itemInfo.itemType = ALLAPPS_ITEMTYPE_APP");
                AppInfo appInfo = (AppInfo) itemInfo;
                boolean zLambda$startActivitySafely$4$Launcher = this.mLauncher.lambda$startActivitySafely$4$Launcher(v, new Intent(appInfo.intent), (ItemInfo) appInfo);
                LGLog.d(str, "Launch. tag=" + appInfo.toString() + " intent=" + appInfo.intent + "startSuccess= " + zLambda$startActivitySafely$4$Launcher);
                if (zLambda$startActivitySafely$4$Launcher && ((AllAppsItemInfo) itemInfo).isSearched) {
                    this.mLauncher.updateSearchedApp(appInfo.componentName);
                    return;
                }
                return;
            }
            if (itemInfo.itemType == 2) {
                onClickFolderIcon(v);
                return;
            }
            return;
        }
        LGLog.d(str, "Abnormal Item type: ItemInfo = " + v.getTag());
    }

    private void onClickFolderIcon(View v) {
        String str = TAG;
        LGLog.d(str, "onClickFolder");
        if (!(v instanceof FolderIcon)) {
            throw new IllegalArgumentException("Input must be a FolderIcon");
        }
        FolderIcon folderIcon = (FolderIcon) v;
        FolderInfo folderInfo = folderIcon.getFolderInfo();
        Folder folderForTag = this.mLauncher.getWorkspace().getFolderForTag(folderInfo);
        if (folderInfo.opened && folderForTag == null) {
            Log.d(str, "Folder info marked as open, but associated folder is not open. Screen: " + folderInfo.screenId + " (" + folderInfo.cellX + ", " + folderInfo.cellY + ")");
            folderInfo.opened = false;
        }
        if (!folderInfo.opened && !folderIcon.getFolder().isDestroyed()) {
            this.mLauncher.closeFolder(false);
            this.mLauncher.openFolder(folderIcon);
        } else if (folderForTag != null) {
            int pageForView = this.mLauncher.getWorkspace().getPageForView(folderForTag);
            this.mLauncher.closeFolder(folderForTag, false);
            if (pageForView != this.mLauncher.getWorkspace().getCurrentPage()) {
                this.mLauncher.closeFolder(false);
                this.mLauncher.openFolder(folderIcon);
            }
        }
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View v) {
        LGLog.d(TAG, String.format("onLonglick [v=%s]", v));
        if (this.mLauncher.isInMultiWindowMode()) {
            Toast.makeText(getContext(), getResources().getString(R.string.home_screen_lock_in_multiwindow), 0).show();
            return true;
        }
        if (HomeSettingsSharedPreferences.getHomescreenLockEnabled(getContext())) {
            Toast.makeText(getContext(), HomeSettingsSharedPreferences.getHomeLockDisableGuideText(getContext()), 0).show();
            return true;
        }
        if (this.mIsKeyDown) {
            this.mIsKeyDown = false;
            return true;
        }
        if (!this.mLauncher.isAppsViewVisible() || this.mLauncher.getWorkspace().isSwitchingState() || !this.mLauncher.isDraggingEnabled()) {
            return false;
        }
        if (this.mLauncher.isLongClickFromKeyEnter) {
            this.mLauncher.isLongClickFromKeyEnter = false;
            return true;
        }
        if (isInArrangeMode()) {
            AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(this.mCurrentPage);
            if (allAppsPagedCellLayout == null || allAppsPagedCellLayout.getShortcutsAndWidgets().indexOfChild(v) < 0 || this.mNextPage != -1 || !this.mLauncher.isAllAppsVisible() || this.mLauncher.getWorkspace().isSwitchingState()) {
                return false;
            }
            boolean zBeginDragging = beginDragging(v);
            snapToPage(getPageNearestToCenterOfScreen(), 0);
            return zBeginDragging;
        }
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && (v instanceof FolderIcon)) {
            return true;
        }
        DragOptions dragOptions = new DragOptions();
        dragOptions.isDragFromAllAps = true;
        if (!LGHomeFeature.isEnableDefaultHome() && !(v instanceof FolderIcon)) {
            this.mLongPressHandler = new Handler();
            this.mHandler.postDelayed(new Runnable() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.18
                @Override // java.lang.Runnable
                public void run() {
                    AllAppsPagedView.this.mDragController.callOnDragStart();
                }
            }, 1500L);
        }
        if (v instanceof BubbleTextView) {
            ((BubbleTextView) v).getLongPressHelper().cancelLongPress();
        }
        cancelLongPress();
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && this.mLauncher.getCarouselLayout() != null) {
            this.mLauncher.getCarouselLayout().beginDragSharedDeepShortcut(v, this, dragOptions);
        } else {
            this.mLauncher.getWorkspace().beginDragSharedDeepShortcut(v, this, dragOptions);
        }
        return false;
    }

    public void cancelLongPressHandler() {
        this.mHandler.removeMessages(0);
    }

    public boolean beginDragging(View v) {
        LGLog.d(TAG, "beginDragging=" + v);
        Resources resources = getResources();
        ((AllAppsItemInfo) v.getTag()).itemView = v;
        Bitmap bitmapCreateDragBitmap = this.mLauncher.getWorkspace().createDragBitmap(v, new AtomicInteger(2));
        setupDragMode();
        int[] iArr = new int[2];
        v.getLocationOnScreen(iArr);
        ViewPosition.getLocationInDragLayer(this.mLauncher.getDragLayer(), v, iArr);
        int width = bitmapCreateDragBitmap.getWidth();
        int width2 = iArr[0] + ((v.getWidth() - width) / 2);
        int i = iArr[1];
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.app_icon_size);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.app_icon_padding_top);
        int paddingTop = v.getPaddingTop();
        int i2 = (width - dimensionPixelSize) / 2;
        int i3 = i + paddingTop;
        Point point = new Point(0, dimensionPixelSize2);
        Rect rect = new Rect(i2, paddingTop, i2 + dimensionPixelSize, dimensionPixelSize + paddingTop);
        this.mdragIndex = v.getId();
        this.mdragPage = this.mCurrentPage;
        this.mLastFolderAniIdx = -1;
        this.mswapIndex = -1;
        if (!isInArrangeMode() && (v instanceof FolderIcon)) {
            this.mDragController.startDragForDeepShortcut(bitmapCreateDragBitmap, width2, i3, this, (ItemInfo) v.getTag(), point, rect, mCellLayout_middle_scale_xFactor * 1.0f, new DragOptions());
        } else {
            this.mDragController.startDragForDeepShortcut(bitmapCreateDragBitmap, width2, i3, this, (ItemInfo) v.getTag(), point, rect, 1.0f, new DragOptions());
        }
        new Paint().setAlpha(80);
        v.setVisibility(4);
        talkbackReadCurrentPosition(true);
        bitmapCreateDragBitmap.recycle();
        return true;
    }

    private boolean testDataReady() {
        AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(0);
        if (allAppsPagedCellLayout == null || allAppsPagedCellLayout.getPageChildCount() <= 0) {
            return !this.mAppUtil.isAllAppsEmpty();
        }
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:34:0x010a, code lost:
    
        return false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean pullCurrentPage(android.view.View r11, int r12, int r13, int r14) {
        /*
            r10 = this;
            r11 = r12
        L1:
            android.view.View r13 = r10.getChildAt(r11)
            com.lge.launcher3.allapps.AllAppsPagedCellLayout r13 = (com.lge.launcher3.allapps.AllAppsPagedCellLayout) r13
            int r0 = r11 + 1
            android.view.View r1 = r10.getChildAt(r0)
            com.lge.launcher3.allapps.AllAppsPagedCellLayout r1 = (com.lge.launcher3.allapps.AllAppsPagedCellLayout) r1
            r2 = 0
            if (r13 == 0) goto L10a
            if (r1 != 0) goto L16
            goto L10a
        L16:
            int r3 = r13.getPageChildCount()
            int r4 = r1.getPageChildCount()
            int r5 = r13.getCountX()
            int r6 = r13.getCountX()
            int r5 = r5 * r6
            r6 = 1
            int r5 = r5 - r6
            r7 = 0
            if (r3 != r5) goto Lb1
            android.view.View r3 = r1.getChildOnPageId(r2)
            r1.removeViewOnPageId(r2)
            if (r3 == 0) goto Lb1
            boolean r2 = r10.isInArrangeMode()
            if (r2 == 0) goto L4c
            int r2 = r12 + 1
            if (r11 != r2) goto L4c
            boolean r2 = r3 instanceof com.android.launcher3.BubbleTextView
            if (r2 == 0) goto L4c
            android.content.Context r2 = r10.mContext
            com.lge.launcher3.uninstallmode.UninstallModeManager r2 = com.lge.launcher3.uninstallmode.UninstallModeManager.getInstance(r2)
            r2.setUninstallTypeForBadgeViewAllApps(r3)
        L4c:
            r3.setTranslationX(r7)
            r3.setTranslationY(r7)
            r3.requestLayout()
            com.lge.launcher3.allapps.AllAppsPagedCellLayoutParam r2 = new com.lge.launcher3.allapps.AllAppsPagedCellLayoutParam
            int r8 = r13.getCountX()
            int r8 = r8 - r6
            int r9 = r13.getCountY()
            int r9 = r9 - r6
            r2.<init>(r8, r9, r6, r6)
            r13.addViewToCellLayout(r3, r5, r5, r2)
            java.lang.Object r2 = r3.getTag()
            com.lge.launcher3.allapps.AllAppsItemInfo r2 = (com.lge.launcher3.allapps.AllAppsItemInfo) r2
            int r8 = r13.getCountX()
            int r8 = r8 - r6
            r2.cellX = r8
            java.lang.Object r2 = r3.getTag()
            com.lge.launcher3.allapps.AllAppsItemInfo r2 = (com.lge.launcher3.allapps.AllAppsItemInfo) r2
            int r13 = r13.getCountY()
            int r13 = r13 - r6
            r2.cellY = r13
            java.lang.Object r13 = r3.getTag()
            com.lge.launcher3.allapps.AllAppsItemInfo r13 = (com.lge.launcher3.allapps.AllAppsItemInfo) r13
            long r8 = (long) r11
            r13.screenId = r8
            java.lang.Object r13 = r3.getTag()
            com.lge.launcher3.allapps.AllAppsItemInfo r13 = (com.lge.launcher3.allapps.AllAppsItemInfo) r13
            r13.requiresDbUpdate = r6
            if (r14 <= 0) goto Lb1
            if (r11 != r12) goto Lb1
            android.view.animation.AlphaAnimation r11 = new android.view.animation.AlphaAnimation
            r13 = 1065353216(0x3f800000, float:1.0)
            r11.<init>(r7, r13)
            r8 = 300(0x12c, double:1.48E-321)
            r11.setDuration(r8)
            long r8 = (long) r14
            r11.setStartOffset(r8)
            com.lge.launcher3.allapps.AllAppsPagedView$19 r13 = new com.lge.launcher3.allapps.AllAppsPagedView$19
            r13.<init>()
            r11.setAnimationListener(r13)
            r3.startAnimation(r11)
        Lb1:
            r11 = r6
        Lb2:
            int r13 = r4 + 1
            if (r11 >= r13) goto L102
            android.view.View r13 = r1.getChildOnPageId(r11)
            if (r13 != 0) goto Lbd
            goto Lff
        Lbd:
            android.view.ViewGroup$LayoutParams r2 = r13.getLayoutParams()
            com.lge.launcher3.allapps.AllAppsPagedCellLayoutParam r2 = (com.lge.launcher3.allapps.AllAppsPagedCellLayoutParam) r2
            int r3 = r11 + (-1)
            int r8 = r10.mCellCountX
            int r8 = r3 % r8
            r2.cellX = r8
            int r8 = r10.mCellCountX
            int r8 = r3 / r8
            r2.cellY = r8
            r2.isLockedToGrid = r6
            r13.setTranslationX(r7)
            r13.setTranslationY(r7)
            r13.requestLayout()
            java.lang.Object r2 = r13.getTag()
            com.lge.launcher3.allapps.AllAppsItemInfo r2 = (com.lge.launcher3.allapps.AllAppsItemInfo) r2
            int r8 = r10.mCellCountX
            int r8 = r3 % r8
            r2.cellX = r8
            java.lang.Object r2 = r13.getTag()
            com.lge.launcher3.allapps.AllAppsItemInfo r2 = (com.lge.launcher3.allapps.AllAppsItemInfo) r2
            int r8 = r10.mCellCountX
            int r8 = r3 / r8
            r2.cellY = r8
            java.lang.Object r2 = r13.getTag()
            com.lge.launcher3.allapps.AllAppsItemInfo r2 = (com.lge.launcher3.allapps.AllAppsItemInfo) r2
            r2.requiresDbUpdate = r6
            r13.setId(r3)
        Lff:
            int r11 = r11 + 1
            goto Lb2
        L102:
            int r5 = r5 + 1
            if (r4 == r5) goto L107
            return r6
        L107:
            r11 = r0
            goto L1
        L10a:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.allapps.AllAppsPagedView.pullCurrentPage(android.view.View, int, int, int):boolean");
    }

    private boolean removeApplication(AppInfo appInfo, boolean immediately) {
        BubbleTextView bubbleTextView;
        AllAppsItemInfo allAppsItemInfoFindMenuItemInfoByAppInfo = this.mMenuItemFactory.findMenuItemInfoByAppInfo(appInfo);
        if (allAppsItemInfoFindMenuItemInfoByAppInfo != null) {
            int i = (allAppsItemInfoFindMenuItemInfoByAppInfo.cellY * this.mCellCountX) + allAppsItemInfoFindMenuItemInfoByAppInfo.cellX;
            AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt((int) allAppsItemInfoFindMenuItemInfoByAppInfo.screenId);
            if (allAppsPagedCellLayout == null || !(allAppsPagedCellLayout.getChildOnPageId(i) instanceof BubbleTextView) || (bubbleTextView = (BubbleTextView) allAppsPagedCellLayout.getChildOnPageId(i)) == null) {
                return false;
            }
            if (this.mMenuItemFactory.removeItemInfo(allAppsItemInfoFindMenuItemInfoByAppInfo)) {
                Log.d(TAG, "removeApplication ItemInfo = " + (allAppsItemInfoFindMenuItemInfoByAppInfo == null ? null : allAppsItemInfoFindMenuItemInfoByAppInfo.componentName));
                if (getVisibility() == 0 && !this.mLauncher.isPaused()) {
                    if (!isInArrangeMode()) {
                        int iRemoveNarrangePage = allAppsPagedCellLayout.removeNarrangePage(i, false);
                        this.mMenuItemFactory.updatePositionChangedItems();
                        return iRemoveNarrangePage >= 0;
                    }
                    setClipChildrenAtPage((int) allAppsItemInfoFindMenuItemInfoByAppInfo.screenId, false);
                    allAppsPagedCellLayout.getShortcutsAndWidgets().setClipChildren(false);
                    allAppsPagedCellLayout.getShortcutsAndWidgets().setClipToPadding(false);
                    shrinkAndFadeOutAllAppsItem(bubbleTextView);
                } else {
                    int iRemoveNarrangePage2 = allAppsPagedCellLayout.removeNarrangePage(i, false);
                    this.mMenuItemFactory.updatePositionChangedItems();
                    return iRemoveNarrangePage2 >= 0;
                }
            } else {
                int iRemoveNarrangePage3 = allAppsPagedCellLayout.removeNarrangePage(i, false);
                this.mMenuItemFactory.updatePositionChangedItems();
                return iRemoveNarrangePage3 >= 0;
            }
        } else {
            if (getVisibility() != 0 || this.mLauncher.isPaused()) {
                return removeFolderItemInfo(appInfo);
            }
            if (immediately) {
                return removeFolderItemInfo(appInfo);
            }
            Folder openFolder = getOpenFolder();
            if (openFolder == null) {
                boolean zRemoveFolderItemInfo = removeFolderItemInfo(appInfo);
                if (!isInArrangeMode()) {
                    removeVacantPage();
                }
                return zRemoveFolderItemInfo;
            }
            if (getTextViewFromFolder(openFolder.getInfo().getContents(), appInfo) == null) {
                return false;
            }
            new Runnable() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.20
                @Override // java.lang.Runnable
                public void run() {
                }
            };
            openFolder.getContent().setClipChildren(false);
            openFolder.getContent().setClipToPadding(false);
        }
        return true;
    }

    public boolean closeFolder(boolean animated, boolean bOrientation) {
        this.mLauncher.closeFolder(animated);
        return false;
    }

    Folder getOpenFolder() {
        return this.mLauncher.getWorkspace().getOpenFolder();
    }

    private void updateFolderItemInfo(AppInfo appInfo) {
        ArrayList<AllAppsItemInfo> allAppsItemInfoList = this.mMenuItemFactory.getAllAppsItemInfoList();
        String strFlattenToShortString = appInfo.componentName.flattenToShortString();
        for (AllAppsItemInfo allAppsItemInfo : allAppsItemInfoList) {
            if (allAppsItemInfo != null && allAppsItemInfo.itemType == 2) {
                AllAppsFolderInfo allAppsFolderInfo = allAppsItemInfo.mFolderInfo;
                ArrayList<ShortcutInfo> contents = allAppsFolderInfo.getContents();
                for (int i = 0; i < contents.size(); i++) {
                    ShortcutInfo shortcutInfo = contents.get(i);
                    Intent intent = shortcutInfo.intent;
                    ComponentName component = intent.getComponent();
                    if (PackageUtils.ANDROID_INTENT_ACTION_MAIN.equals(intent.getAction()) && component != null && strFlattenToShortString.equals(component.flattenToShortString())) {
                        shortcutInfo.setIcon(this.mIconCache.getIcon(shortcutInfo.intent, shortcutInfo.user));
                        shortcutInfo.runtimeStatusFlags = appInfo.runtimeStatusFlags;
                        shortcutInfo.title = appInfo.title;
                        if (allAppsItemInfo.itemView != null) {
                            allAppsFolderInfo.itemsChanged();
                            FolderIcon folderIcon = (FolderIcon) allAppsItemInfo.itemView;
                            BubbleTextView bubbleTextView = (BubbleTextView) folderIcon.getFolder().getViewForInfo(shortcutInfo);
                            if (bubbleTextView != null) {
                                bubbleTextView.applyFromShortcutInfo(shortcutInfo, this.mIconCache);
                                bubbleTextView.invalidate();
                            }
                            folderIcon.invalidate();
                        }
                    }
                }
            }
        }
    }

    private boolean removeFolderItemInfo(AppInfo appInfo) {
        ArrayList<AllAppsItemInfo> allAppsItemInfoList = this.mMenuItemFactory.getAllAppsItemInfoList();
        String strFlattenToShortString = appInfo.componentName.flattenToShortString();
        Iterator<AllAppsItemInfo> it = allAppsItemInfoList.iterator();
        while (true) {
            if (!it.hasNext()) {
                return false;
            }
            AllAppsItemInfo next = it.next();
            if (next != null && next.itemType == 2) {
                AllAppsFolderInfo allAppsFolderInfo = next.mFolderInfo;
                ArrayList<ShortcutInfo> contents = allAppsFolderInfo.getContents();
                for (int i = 0; i < contents.size(); i++) {
                    ShortcutInfo shortcutInfo = contents.get(i);
                    Intent intent = shortcutInfo.intent;
                    ComponentName component = intent.getComponent();
                    if (PackageUtils.ANDROID_INTENT_ACTION_MAIN.equals(intent.getAction()) && component != null && strFlattenToShortString.equals(component.flattenToShortString())) {
                        allAppsFolderInfo.remove(shortcutInfo);
                        AllAppsItemFactory.getInstance().removeFolderItem(allAppsFolderInfo, shortcutInfo);
                        return true;
                    }
                }
            }
        }
    }

    public void changeCellCountXY(int changeCountX, int changeCountY) {
        Message messageObtainMessage = this.mHandler.obtainMessage(4);
        messageObtainMessage.what = 4;
        this.mHandler.sendMessage(messageObtainMessage);
        Message messageObtainMessage2 = this.mHandler.obtainMessage(1);
        messageObtainMessage2.what = 1;
        messageObtainMessage2.arg1 = changeCountX;
        messageObtainMessage2.arg2 = changeCountY;
        this.mHandler.sendMessageDelayed(messageObtainMessage2, 200L);
    }

    void closeMenuDialog() {
        if (LGHomeFeature.Config.FEATURE_SUPPORT_LANDSCAPE.getValue()) {
            return;
        }
        this.mLauncher.lockScreenOrientation();
    }

    void reloadApps() {
        this.mHandler.sendEmptyMessageDelayed(0, 200L);
    }

    void reloadApps(ArrayList<AppInfo> apps) {
        this.mAppUtil.setApps(apps);
        this.mHandler.sendEmptyMessageDelayed(0, 200L);
    }

    void setReloadState(final ArrayList<AppInfo> apps, final boolean needreload) {
        this.mCurrentApps = (ArrayList) apps.clone();
        this.mNeedReload = needreload;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processReloadState() {
        if (this.mLauncher.isPaused()) {
            return;
        }
        if (this.mCurrentApps != null) {
            if (this.mLauncher.getModel().isAllAppsLoaded()) {
                this.mCurrentApps = this.mLauncher.mModel.getAllAppsList();
            }
            setApps(this.mCurrentApps);
            if (this.mNeedReload) {
                this.mNeedReload = false;
                reloadApps(this.mCurrentApps);
            }
            this.mCurrentApps = null;
            return;
        }
        if (this.mLauncher.getModel().isAllAppsLoaded() && !this.mIsAllAppsLoaded) {
            ArrayList<AppInfo> allAppsList = this.mLauncher.mModel.getAllAppsList();
            this.mCurrentApps = allAppsList;
            setApps(allAppsList);
        }
        this.mCurrentApps = null;
        LGLog.d(TAG, "processReloadState(), mCurrentApps is null");
    }

    void removeOrphanItems(String packageName, UserHandle user) {
        ComponentName componentName;
        String packageName2;
        ArrayList<AllAppsItemInfo> allAppsItemInfoList = this.mMenuItemFactory.getAllAppsItemInfoList();
        ArrayList<AppInfo> arrayList = new ArrayList<>();
        for (AllAppsItemInfo allAppsItemInfo : allAppsItemInfoList) {
            if (allAppsItemInfo != null && allAppsItemInfo.itemType == 0 && (componentName = allAppsItemInfo.componentName) != null && (packageName2 = componentName.getPackageName()) != null && packageName2.equals(packageName) && user != null && allAppsItemInfo != null && user.equals(allAppsItemInfo.user)) {
                arrayList.add(allAppsItemInfo);
            }
        }
        removeApps(arrayList);
    }

    void searchApps(String key) {
        if (this.mAppUtil.isAllAppsEmpty()) {
            return;
        }
        if (key.equals(this.mSearchUtil.getSearchWord())) {
            setCurrentPageAftterSearchLoading();
        } else {
            this.mSearchUtil.searchApps(key);
        }
    }

    void setSearchKeyword(String key) {
        this.mSearchUtil.setSearchKeyword(key);
    }

    private void searchAppsDirect() {
        ArrayList<AllAppsItemInfo> arrayList = new ArrayList<>(this.mMenuItemFactory.getAllAppsItemInfoList());
        boolean zSearchForKeyWord = this.mSearchUtil.searchForKeyWord(arrayList);
        boolean z = true;
        if (zSearchForKeyWord) {
            this.mMenuItemFactory.convertDatabaseLayout(this.mSearchUtil.getSearchResult());
            setSpannable(this.mSearchUtil.getSearchResult());
            loadAllAppsListForSearch(this.mSearchUtil.getSearchResult(), zSearchForKeyWord);
            if (this.mSearchUtil.getSearchResult().size() <= 0) {
                z = false;
            }
        } else {
            clearSpannable(getAllAppsItemInfoList());
            loadAllAppsListForSearch(arrayList, zSearchForKeyWord);
            appUpdateStart();
        }
        IAllAppsHostListener iAllAppsHostListener = this.mHostListener;
        if (iAllAppsHostListener != null) {
            iAllAppsHostListener.setSearchComplete(z);
        }
    }

    public void resetAllAppsLoadedBySearchBar() {
        if (this.mIsShowSearchBar) {
            this.mIsAllAppsLoaded = false;
        }
    }

    void setShowSearchBar(boolean bShow) {
        this.mIsShowSearchBar = bShow;
    }

    public ArrayList<AllAppsItemInfo> getAllAppInfoList(boolean isIncludeFolderItem) {
        AllAppsFolderInfo allAppsFolderInfo;
        ArrayList<AllAppsItemInfo> arrayList = new ArrayList<>();
        for (AllAppsItemInfo allAppsItemInfo : this.mMenuItemFactory.getAllAppsItemInfoList()) {
            if (allAppsItemInfo != null) {
                if (allAppsItemInfo.itemType != 2) {
                    arrayList.add(allAppsItemInfo);
                } else if (isIncludeFolderItem && (allAppsFolderInfo = allAppsItemInfo.mFolderInfo) != null) {
                    for (ShortcutInfo shortcutInfo : allAppsFolderInfo.getContents()) {
                        AppInfo appInfoFindAppByComponent = this.mAppUtil.findAppByComponent(shortcutInfo.intent.getComponent(), shortcutInfo.user);
                        if (appInfoFindAppByComponent != null) {
                            arrayList.add(new AllAppsItemInfo(appInfoFindAppByComponent));
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    private void rearrangeBySortType(AllAppsSort.SortType sortType) {
        Folder openFolder = getOpenFolder();
        closeFolder(false, false);
        if (isInArrangeMode()) {
            setIsPageMoving(false);
            this.mWasInArrangeMode = true;
            endArrangeMode(false);
        }
        this.mMenuItemFactory.rearrangeBySortType(sortType);
        loadAllAppsList();
        UninstallModeManager.getInstance(this.mContext).setUninstallTypeForAllBadgeViews(this);
        if (this.mWasInArrangeMode) {
            setIsPageMoving(false);
            startArrangeMode(false);
            this.mWasInArrangeMode = false;
        }
        if (openFolder != null) {
            Message messageObtainMessage = this.mHandler.obtainMessage(6);
            Long lValueOf = Long.valueOf(openFolder.getInfo().id);
            messageObtainMessage.what = 6;
            messageObtainMessage.obj = lValueOf;
            this.mHandler.sendMessageDelayed(messageObtainMessage, 100L);
        }
    }

    void openFolder(long folderid, boolean animated, boolean bOrientation) {
        for (AllAppsItemInfo allAppsItemInfo : this.mMenuItemFactory.getAllAppsItemInfoList()) {
            if (allAppsItemInfo != null && allAppsItemInfo.itemType == 2) {
                long j = allAppsItemInfo.id;
            }
        }
    }

    public boolean isInArrangeMode() {
        return this.mArrangeMode;
    }

    public int getAppsCount() {
        return this.mAppUtil.getAllAppsCount();
    }

    public int getAllAppsCount() {
        return AllAppsItemFactory.getInstance().getAllAppsItemInfoList().size();
    }

    private final boolean isMenuStopState() {
        boolean zIsPaused = this.mLauncher.isPaused();
        if (zIsPaused || this.mSearchUtil.isSearchState()) {
            return true;
        }
        return (getVisibility() == 0 || zIsPaused || this.mAppUtil.getRemainBindAppsSize() <= 0) ? false : true;
    }

    @Override // com.lge.launcher3.allapps.AllAppsSearchUtil.ISearchCallback
    public AppInfo getAppInfo(ShortcutInfo shortcutInfo) {
        return this.mAppUtil.getAppInfo(shortcutInfo.intent.getComponent(), shortcutInfo.user);
    }

    @Override // com.lge.launcher3.allapps.AllAppsSearchUtil.ISearchCallback
    public ArrayList<AllAppsItemInfo> getAllAppsItemInfoList() {
        return this.mMenuItemFactory.getAllAppsItemInfoList();
    }

    @Override // com.lge.launcher3.allapps.AllAppsSearchUtil.ISearchCallback
    public void searchResult(boolean keyvalue) {
        boolean z = true;
        if (keyvalue) {
            this.mMenuItemFactory.convertDatabaseLayout(this.mSearchUtil.getSearchResult());
            setSpannable(this.mSearchUtil.getSearchResult());
            loadAllAppsListForSearch(this.mSearchUtil.getSearchResult(), true);
            if (this.mSearchUtil.getSearchResult().size() <= 0) {
                z = false;
            }
        } else {
            clearSpannable(getAllAppsItemInfoList());
            loadAllAppsListForSearch(getAllAppsItemInfoList(), keyvalue);
            appUpdateStart();
        }
        if (!this.mIsShowSearchBar) {
            setChildFocus();
        }
        IAllAppsHostListener iAllAppsHostListener = this.mHostListener;
        if (iAllAppsHostListener != null) {
            iAllAppsHostListener.setSearchComplete(z);
        }
    }

    public void setChildFocus() {
        AllAppsPagedCellLayout allAppsPagedCellLayout;
        if (!LGFeatureConfig.isFolderPhone(this.mContext) || (allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(this.mCurrentPage)) == null) {
            return;
        }
        allAppsPagedCellLayout.setChildFocus();
        setFocusableInTouchMode(false);
    }

    @Override // com.lge.launcher3.PagedView
    public String getCurrentPageDescription() {
        int i = this.mNextPage != -1 ? this.mNextPage : this.mCurrentPage;
        String str = getResources().getString(R.string.all_apps_button_label) + String.format(this.mContext.getString(R.string.default_scroll_format), Integer.valueOf(i + 1), Integer.valueOf(getChildCount()));
        AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(this.mCurrentPage);
        if (allAppsPagedCellLayout == null) {
            return str;
        }
        return str + "," + this.mContext.getString(R.string.talkback_gird_locate_lines_rows, Integer.valueOf(allAppsPagedCellLayout.getPageChildCount() < this.mCellCountX ? allAppsPagedCellLayout.getPageChildCount() : this.mCellCountX), Integer.valueOf(((allAppsPagedCellLayout.getPageChildCount() + this.mCellCountX) - 1) / this.mCellCountX));
    }

    @Override // com.android.launcher3.DropTarget
    public void getHitRectRelativeToDragLayer(Rect outRect) {
        if (isInArrangeMode()) {
            Display defaultDisplay = this.mLauncher.getWindowManager().getDefaultDisplay();
            Point point = new Point();
            defaultDisplay.getRealSize(point);
            outRect.set(0, 0, point.x, point.y);
            return;
        }
        super.getHitRect(outRect);
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsDeleteDropTarget() {
        return !LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue();
    }

    public void setHostListener(IAllAppsHostListener listener) {
        this.mHostListener = listener;
    }

    public void setFocusIndicatorView(FocusIndicatorView indicatorView) {
        AllAppsTextViewMngr allAppsTextViewMngr = new AllAppsTextViewMngr(this.mContext, this);
        this.mTextViewPool = allAppsTextViewMngr;
        allAppsTextViewMngr.setClickListener(this, this, this, indicatorView);
        this.mFocusIndicatorView = indicatorView;
    }

    protected void cancelChildLongPress() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt(i);
            if (allAppsPagedCellLayout != null) {
                int childCount2 = allAppsPagedCellLayout.getShortcutsAndWidgets().getChildCount();
                for (int i2 = 0; i2 < childCount2; i2++) {
                    View childAt = allAppsPagedCellLayout.getShortcutsAndWidgets().getChildAt(i2);
                    if (childAt != null) {
                        childAt.cancelLongPress();
                    }
                }
            }
        }
    }

    void restorePageForIndex(int index) {
        if (index < 0) {
            return;
        }
        this.mSaveInstanceStateItemIndex = index;
        this.mSearchSavedItemIndex = index;
    }

    public int getDefaultPage() {
        int i = this.mSaveInstanceStateItemIndex;
        return i == -1 ? this.mCurrentPage : i;
    }

    public void sendTalkBackDescription() {
        TalkBackUtils.isEnabled(this.mContext);
    }

    public void mapOverItems(boolean recurse, Workspace.ItemOperator op) {
        View view;
        for (AllAppsItemInfo allAppsItemInfo : this.mMenuItemFactory.getAllAppsItemInfoList()) {
            if (allAppsItemInfo != null && (view = allAppsItemInfo.itemView) != null) {
                if (recurse && allAppsItemInfo.itemType == 2 && (view instanceof FolderIcon)) {
                    FolderIcon folderIcon = (FolderIcon) view;
                    ArrayList<View> itemsInReadingOrder = folderIcon.getFolder().getItemsInReadingOrder();
                    int size = itemsInReadingOrder.size();
                    for (int i = 0; i < size; i++) {
                        View view2 = itemsInReadingOrder.get(i);
                        if (view2 != null && op.evaluate((ItemInfo) view2.getTag(), view2, folderIcon)) {
                            return;
                        }
                    }
                } else if (op.evaluate(allAppsItemInfo, view, null)) {
                    return;
                }
            }
        }
    }

    public void restoreArrangeMode(boolean set) {
        this.mRestoredArrangeMode = set;
    }

    public boolean onBackPressed() {
        if (!isInArrangeMode()) {
            return false;
        }
        endArrangeMode(true);
        return true;
    }

    public void setArrangeModeBg(boolean inArrangeMode) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            setPageArrangeModeBg(inArrangeMode, i);
        }
    }

    void setIsPageMoving(boolean bIsPageMoving) {
        setPageMoving(bIsPageMoving);
        if (this.mScroller.isFinished()) {
            return;
        }
        this.mScroller.abortAnimation();
        if (this.mNextPage != -1) {
            setCurrentPage(Math.max(0, Math.min(this.mNextPage, getPageCount() - 1)));
            this.mNextPage = -1;
        }
    }

    public int getRelativeChildOffset(int index) {
        return this.mPaddingLeft + (((getMeasuredWidth() - (this.mPaddingLeft + this.mPaddingRight)) - getChildWidth(index)) / 2);
    }

    public int getChildWidth(int index) {
        View pageAt = getPageAt(index);
        if (pageAt == null) {
            return -1;
        }
        return pageAt.getMeasuredWidth();
    }

    public void removeItemsInAllApps(ArrayList<ShortcutInfo> shortcutInfos) {
        Iterator<ShortcutInfo> it = shortcutInfos.iterator();
        while (it.hasNext()) {
            AllAppsItemInfo allAppsItemInfoByShortcutInfo = this.mMenuItemFactory.getAllAppsItemInfoByShortcutInfo(it.next());
            if (allAppsItemInfoByShortcutInfo != null && allAppsItemInfoByShortcutInfo.itemView != null) {
                this.mMenuItemFactory.removeItemInfo(allAppsItemInfoByShortcutInfo);
                removeItemInAllApps(allAppsItemInfoByShortcutInfo.itemView, allAppsItemInfoByShortcutInfo.screenId);
            }
        }
    }

    public void removeItemInAllApps(View v, long id) {
        ((AllAppsPagedCellLayout) getChildAt((int) id)).removeNarrangePage(v.getId(), false);
        this.mMenuItemFactory.updatePositionChangedItems();
    }

    public void addItemsInAllApps(ArrayList<ShortcutInfo> shortcutInfos) {
        for (ShortcutInfo shortcutInfo : shortcutInfos) {
            if (shortcutInfo != null) {
                addItemInAllApps(shortcutInfo);
            }
        }
    }

    public void addItemInAllApps(ShortcutInfo shortcutInfo) {
        AppInfo appInfoFindAppByComponent = this.mAppUtil.findAppByComponent(shortcutInfo.getTargetComponent(), shortcutInfo.user);
        if (appInfoFindAppByComponent != null) {
            addNewApplicationToLastPage(appInfoFindAppByComponent, true, false);
        } else {
            LGLog.d(TAG, "fail addItemInAllApps because appInfo can't found by component");
        }
    }

    public void removeVacantAllAppsItem() {
        ((AllAppsPagedCellLayout) getChildAt(this.mCurrentPage)).removeNarrangePage(this.mdragIndex, false);
        this.mMenuItemFactory.updatePositionChangedItems();
    }

    public void shrinkAndFadeOutAllAppsItem(final View view) {
        if (view == null) {
            return;
        }
        if (Utilities.isPowerSaveMode(view.getContext())) {
            AllAppsPagedCellLayout allAppsPagedCellLayout = (AllAppsPagedCellLayout) getChildAt((int) ((ItemInfo) view.getTag()).screenId);
            if (allAppsPagedCellLayout != null) {
                allAppsPagedCellLayout.removeNarrangePage(view.getId(), false);
            } else {
                LGLog.d(TAG, "can't remove view(" + view + ") because parent cell is null");
            }
            removeVacantPage();
            return;
        }
        ObjectAnimator objectAnimatorOfPropertyValuesHolder = LauncherAnimUtils.ofPropertyValuesHolder(view, PropertyValuesHolder.ofFloat("alpha", 0.0f), PropertyValuesHolder.ofFloat("scaleX", 0.0f), PropertyValuesHolder.ofFloat("scaleY", 0.0f));
        objectAnimatorOfPropertyValuesHolder.setDuration(450L);
        objectAnimatorOfPropertyValuesHolder.addListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.22
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                AllAppsPagedCellLayout allAppsPagedCellLayout2 = (AllAppsPagedCellLayout) AllAppsPagedView.this.getChildAt((int) ((ItemInfo) view.getTag()).screenId);
                if (allAppsPagedCellLayout2 != null) {
                    allAppsPagedCellLayout2.removeNarrangePage(view.getId(), true);
                } else {
                    LGLog.d(AllAppsPagedView.TAG, "can't remove view(" + view + ") because parent cell is null");
                }
                AllAppsPagedView.this.removeVacantPage();
            }
        });
        objectAnimatorOfPropertyValuesHolder.start();
    }

    @Override // com.lge.launcher3.PagedView
    protected void overScroll(float amount) {
        dampedOverScroll(amount);
    }

    public void superScrollTo(int x, int y, int allAppsCurrentPage) {
        super.superScrollTo(x, y);
        updateScrollDirection(allAppsCurrentPage, x);
        updateOverscrollState(x);
        updateWhichPageToDraw(allAppsCurrentPage, x);
        updateFixedOverscrollState(allAppsCurrentPage);
        updateScrollProgress(allAppsCurrentPage, x);
    }

    @Override // com.lge.launcher3.PagedView
    public void dampedOverScroll(float amount) {
        int i = (int) amount;
        if (!isEnableLoop()) {
            i = 0;
        }
        if (amount <= 0.0f) {
            superScrollTo(i, getScrollY(), this.mCurrentPage);
        } else {
            superScrollTo(this.mMaxScroll + i, getScrollY(), this.mCurrentPage);
        }
        invalidate();
    }

    private void updateScrollDirection(int allAppsCurrentPage, int scrollX) {
        int scrollForPage = getScrollForPage(allAppsCurrentPage);
        if (scrollX < scrollForPage) {
            this.scrollDirection = ScreenEffectConst.ScrollDirection.TO_LEFT;
        } else if (scrollForPage < scrollX) {
            this.scrollDirection = ScreenEffectConst.ScrollDirection.TO_RIGHT;
        } else {
            this.scrollDirection = ScreenEffectConst.ScrollDirection.NONE;
        }
    }

    private void updateOverscrollState(int scrollX) {
        if (scrollX < 0) {
            this.overscrollState = ScreenEffectConst.OverscrollState.OVERSCROLL_LEFT;
        } else if (this.mMaxScroll < scrollX) {
            this.overscrollState = ScreenEffectConst.OverscrollState.OVERSCROLL_RIGHT;
        } else {
            this.overscrollState = ScreenEffectConst.OverscrollState.NONE;
        }
    }

    private void updateWhichPageToDraw(int currentPage, int scrollX) {
        if (!isEnableLoop() && isOverscrollLeft()) {
            this.whichPageToDraw = ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_LEFT;
            return;
        }
        if (!isEnableLoop() && isOverscrollRight()) {
            this.whichPageToDraw = ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_RIGHT;
            return;
        }
        int scrollForPageLoop = scrollX - getScrollForPageLoop(indexOfChild(getChildAt(currentPage)));
        if (scrollForPageLoop > 0) {
            this.whichPageToDraw = ScreenEffectConst.WhichPageToDraw.NORMAL_LEFT;
        } else if (scrollForPageLoop < 0) {
            this.whichPageToDraw = ScreenEffectConst.WhichPageToDraw.NORMAL_RIGHT;
        } else {
            this.whichPageToDraw = ScreenEffectConst.WhichPageToDraw.NONE;
        }
    }

    private void updateFixedOverscrollState(int scrollX) {
        this.fixedOverscrollState = ScreenEffectConst.FixedOverscrollState.NONE;
        int i = AnonymousClass26.$SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[this.whichPageToDraw.ordinal()];
        if (i == 1) {
            if (scrollX < 0) {
                this.fixedOverscrollState = ScreenEffectConst.FixedOverscrollState.INNER;
                return;
            } else {
                if (scrollX > 0) {
                    this.fixedOverscrollState = ScreenEffectConst.FixedOverscrollState.OUTER;
                    return;
                }
                return;
            }
        }
        if (i != 2) {
            return;
        }
        int i2 = this.mMaxScroll;
        if (i2 < scrollX) {
            this.fixedOverscrollState = ScreenEffectConst.FixedOverscrollState.INNER;
        } else if (i2 > scrollX) {
            this.fixedOverscrollState = ScreenEffectConst.FixedOverscrollState.OUTER;
        }
    }

    /* JADX INFO: renamed from: com.lge.launcher3.allapps.AllAppsPagedView$26, reason: invalid class name */
    static /* synthetic */ class AnonymousClass26 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw;

        static {
            int[] iArr = new int[ScreenEffectConst.WhichPageToDraw.values().length];
            $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw = iArr;
            try {
                iArr[ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.NORMAL_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.NORMAL_RIGHT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.NONE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    @Override // com.lge.launcher3.PagedView
    public void updateMaxScrollX() {
        super.updateMaxScrollX();
        updateMaxScroll();
    }

    public int getChildMeasuredWidth(int index) {
        View childAt = getChildAt(index);
        if (childAt == null) {
            return 0;
        }
        return childAt.getMeasuredWidth();
    }

    @Override // com.lge.launcher3.PagedView, android.view.ViewGroup
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        canvas.save();
        translateCanvasForLoop(canvas, child);
        boolean zDrawChild = super.drawChild(canvas, child, drawingTime);
        canvas.restore();
        return zDrawChild;
    }

    public void translateCanvasForLoop(Canvas canvas, View child) {
        boolean zIsHeadPage = isHeadPage(child);
        boolean zIsTailPage = isTailPage(child);
        if (isOverscrollLeft() && (this.mIsRtl ? zIsHeadPage : zIsTailPage)) {
            canvas.translate(-this.mMaxScrollForLoop, 0.0f);
            return;
        }
        if (isOverscrollRight()) {
            if (this.mIsRtl) {
                if (!zIsTailPage) {
                    return;
                }
            } else if (!zIsHeadPage) {
                return;
            }
            canvas.translate(this.mMaxScrollForLoop, 0.0f);
        }
    }

    public void updateMaxScroll() {
        int iIndexOfHead = indexOfHead();
        int iIndexOfTail = indexOfTail();
        int childMeasuredWidth = getChildMeasuredWidth(iIndexOfHead);
        int childMeasuredWidth2 = getChildMeasuredWidth(iIndexOfTail);
        int i = this.mMaxScroll;
        this.mMaxScrollForLoop = i;
        if (!this.mIsRtl) {
            childMeasuredWidth = childMeasuredWidth2;
        }
        this.mMaxScrollForLoop = i + childMeasuredWidth + getPageSpacing();
    }

    private boolean isScrollingOverlay() {
        return (this.mIsRtl && this.mUnboundedScrollX > this.mMaxScroll) || (!this.mIsRtl && this.mUnboundedScrollX < 0);
    }

    @Override // com.lge.launcher3.PagedView
    protected int getUnboundedScrollX() {
        if (isScrollingOverlay()) {
            return this.mUnboundedScrollX;
        }
        return super.getUnboundedScrollX();
    }

    public void updatePageScrollsForLoop() {
        int iIndexOfHead = indexOfHead();
        int iIndexOfTail = indexOfTail();
        int childMeasuredWidth = getChildMeasuredWidth(iIndexOfHead);
        int childMeasuredWidth2 = getChildMeasuredWidth(iIndexOfTail);
        int pageSpacing = getPageSpacing();
        int i = !this.mIsRtl ? -1 : 1;
        this.mPrevHeadPageScroll = getScrollForPage(iIndexOfHead) + ((childMeasuredWidth + pageSpacing) * i);
        this.mNextTailPageScroll = getScrollForPage(iIndexOfTail) + (i * (-1) * (childMeasuredWidth2 + pageSpacing));
    }

    public boolean isHeadToTailScrollOver(int scrollX) {
        if (this.mIsRtl) {
            if (scrollX >= this.mPrevHeadPageScroll) {
                return true;
            }
        } else if (scrollX <= this.mPrevHeadPageScroll) {
            return true;
        }
        return false;
    }

    public boolean isTailToHeadScrollOver(int scrollX) {
        if (this.mIsRtl) {
            if (scrollX <= this.mNextTailPageScroll) {
                return true;
            }
        } else if (scrollX >= this.mNextTailPageScroll) {
            return true;
        }
        return false;
    }

    public int[] computeScrollToForLoop(int currentPage, int scrollX) {
        int i;
        int i2;
        if (isHeadToTailScrollOver(scrollX)) {
            currentPage = indexOfTail();
            i = this.mIsRtl ? -1 : 1;
            i2 = this.mMaxScrollForLoop;
        } else {
            if (isTailToHeadScrollOver(scrollX)) {
                currentPage = indexOfHead();
                i = this.mIsRtl ? 1 : -1;
                i2 = this.mMaxScrollForLoop;
            }
            return new int[]{currentPage, scrollX};
        }
        scrollX += i * i2;
        return new int[]{currentPage, scrollX};
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x002b, code lost:
    
        r0 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0048, code lost:
    
        r0 = true;
     */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0039 A[PHI: r0
      0x0039: PHI (r0v7 boolean) = (r0v4 boolean), (r0v4 boolean), (r0v15 boolean), (r0v15 boolean) binds: [B:39:0x0054, B:36:0x004f, B:23:0x0037, B:20:0x0032] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:25:0x003b A[PHI: r0
      0x003b: PHI (r0v12 boolean) = (r0v4 boolean), (r0v4 boolean), (r0v15 boolean), (r0v15 boolean) binds: [B:39:0x0054, B:36:0x004f, B:23:0x0037, B:20:0x0032] A[DONT_GENERATE, DONT_INLINE]] */
    @Override // com.lge.launcher3.PagedView, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void scrollTo(int r5, int r6) {
        /*
            r4 = this;
            boolean r0 = r4.isEnableLoop()
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L1a
            int r0 = r4.mCurrentPage
            int[] r5 = r4.computeScrollToForLoop(r0, r5)
            int r0 = r4.mCurrentPage
            r3 = r5[r1]
            if (r0 == r3) goto L18
            r0 = r5[r1]
            r4.mCurrentPage = r0
        L18:
            r5 = r5[r2]
        L1a:
            boolean r0 = r4.isEnableLoop()
            if (r0 == 0) goto L3d
            boolean r0 = r4.mIsRtl
            if (r0 == 0) goto L29
            int r0 = r4.mMaxScrollForLoop
            if (r5 <= r0) goto L2d
            goto L2b
        L29:
            if (r5 >= 0) goto L2d
        L2b:
            r0 = r2
            goto L2e
        L2d:
            r0 = r1
        L2e:
            boolean r3 = r4.mIsRtl
            if (r3 == 0) goto L35
            if (r5 >= 0) goto L3b
            goto L39
        L35:
            int r3 = r4.mMaxScrollForLoop
            if (r5 <= r3) goto L3b
        L39:
            r3 = r2
            goto L57
        L3b:
            r3 = r1
            goto L57
        L3d:
            boolean r0 = r4.mIsRtl
            if (r0 == 0) goto L46
            int r0 = r4.mMaxScroll
            if (r5 <= r0) goto L4a
            goto L48
        L46:
            if (r5 >= 0) goto L4a
        L48:
            r0 = r2
            goto L4b
        L4a:
            r0 = r1
        L4b:
            boolean r3 = r4.mIsRtl
            if (r3 == 0) goto L52
            if (r5 >= 0) goto L3b
            goto L39
        L52:
            int r3 = r4.mMaxScroll
            if (r5 <= r3) goto L3b
            goto L39
        L57:
            if (r0 == 0) goto L71
            boolean r6 = r4.mAllowOverScroll
            if (r6 == 0) goto La9
            r4.mWasInOverscroll = r2
            boolean r6 = r4.mIsRtl
            if (r6 == 0) goto L6c
            int r6 = r4.mMaxScroll
            int r6 = r5 - r6
            float r6 = (float) r6
            r4.overScroll(r6)
            goto La9
        L6c:
            float r6 = (float) r5
            r4.overScroll(r6)
            goto La9
        L71:
            if (r3 == 0) goto L9a
            boolean r6 = r4.mAllowOverScroll
            if (r6 == 0) goto La9
            r4.mWasInOverscroll = r2
            boolean r6 = r4.mIsRtl
            if (r6 == 0) goto L82
            float r6 = (float) r5
            r4.overScroll(r6)
            goto La9
        L82:
            boolean r6 = r4.isEnableLoop()
            if (r6 == 0) goto L91
            int r6 = r4.mMaxScrollForLoop
            int r6 = r5 - r6
            float r6 = (float) r6
            r4.overScroll(r6)
            goto La9
        L91:
            int r6 = r4.mMaxScroll
            int r6 = r5 - r6
            float r6 = (float) r6
            r4.overScroll(r6)
            goto La9
        L9a:
            boolean r0 = r4.mWasInOverscroll
            if (r0 == 0) goto La4
            r0 = 0
            r4.overScroll(r0)
            r4.mWasInOverscroll = r1
        La4:
            int r0 = r4.mCurrentPage
            r4.superScrollTo(r5, r6, r0)
        La9:
            float r6 = (float) r5
            r4.mTouchX = r6
            r4.mUnboundedScrollX = r5
            long r5 = java.lang.System.nanoTime()
            float r5 = (float) r5
            r6 = 1315859240(0x4e6e6b28, float:1.0E9)
            float r5 = r5 / r6
            r4.mSmoothingTime = r5
            boolean r5 = r4.isReordering(r2)
            if (r5 == 0) goto Ld2
            float r5 = r4.mParentDownMotionX
            float r6 = r4.mParentDownMotionY
            float[] r5 = r4.mapPointFromParentToView(r4, r5, r6)
            r6 = r5[r1]
            r4.mLastMotionX = r6
            r5 = r5[r2]
            r4.mLastMotionY = r5
            r4.updateDragViewTranslationDuringDrag()
        Ld2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.allapps.AllAppsPagedView.scrollTo(int, int):void");
    }

    @Override // com.lge.launcher3.PagedView
    public int validateNewPage(int newPage) {
        if (isEnableLoop()) {
            return Math.max(-1, Math.min(newPage, getPageCount()));
        }
        return super.validateNewPage(newPage);
    }

    @Override // com.lge.launcher3.PagedView
    public void setCurrentPage(int currentPage) {
        if (isEnableLoop()) {
            currentPage = super.validateNewPage(currentPage);
        }
        super.setCurrentPage(currentPage);
    }

    @Override // com.lge.launcher3.PagedView
    public int getCurrentPage() {
        return this.mCurrentPage;
    }

    @Override // com.lge.launcher3.PagedView
    public void snapToDestination() {
        snapToPage(getPageNearestToCenterOfScreen(), PAGE_SNAP_ANIMATION_DURATION);
    }

    @Override // com.lge.launcher3.PagedView
    public boolean snapToPage(int whichPage, int duration, boolean immediate, TimeInterpolator interpolator) {
        int scrollForPageLoop;
        int iValidateNewPage = validateNewPage(whichPage);
        if (!isEnableLoop()) {
            scrollForPageLoop = getScrollForPage(iValidateNewPage);
        } else {
            scrollForPageLoop = getScrollForPageLoop(iValidateNewPage);
        }
        return snapToPage(iValidateNewPage, scrollForPageLoop - this.mUnboundedScrollX, duration, immediate, interpolator);
    }

    public int validateNewPageForLoop(int whichPage) {
        int iIndexOfHead = indexOfHead();
        int iIndexOfTail = indexOfTail();
        return whichPage < iIndexOfHead ? iIndexOfTail : iIndexOfTail < whichPage ? iIndexOfHead : whichPage;
    }

    public boolean isTailPage(int index) {
        return index == indexOfTail();
    }

    public boolean isHeadPage(int index) {
        return index == indexOfHead();
    }

    public boolean isHeadPage(View child) {
        return indexOfChild(child) == indexOfHead();
    }

    public boolean isTailPage(View child) {
        return indexOfChild(child) == indexOfTail();
    }

    public int indexOfTail() {
        return getChildCount() - 1;
    }

    public int indexOfTail(PagedView pagedView) {
        return getChildCount(pagedView) - 1;
    }

    public int getChildCount(PagedView pagedView) {
        return pagedView.getChildCount();
    }

    @Override // com.lge.launcher3.PagedView
    public boolean snapToPage(int whichPage, int delta, int duration, boolean immediate, TimeInterpolator interpolator) {
        int i;
        int iValidateNewPage = validateNewPage(whichPage);
        if (isEnableLoop()) {
            iValidateNewPage = validateNewPageForLoop(iValidateNewPage);
        }
        this.mNextPage = iValidateNewPage;
        pageBeginTransition();
        awakenScrollBars(duration);
        if (immediate) {
            i = 0;
        } else {
            if (duration == 0) {
                duration = Math.abs(delta);
            }
            i = duration;
        }
        if (!this.mScroller.isFinished()) {
            abortScrollerAnimation(false);
        }
        if (interpolator != null) {
            this.mScroller.setInterpolator(interpolator);
        } else {
            this.mScroller.setInterpolator(this.mDefaultInterpolator);
        }
        this.mScroller.startScroll(this.mUnboundedScrollX, 0, delta, 0, i);
        ignoreReorderingUpdatePageIndicator();
        if (this.mMenuItemFactory.getManagedProfileStartPage() == -1 || iValidateNewPage < this.mMenuItemFactory.getManagedProfileStartPage()) {
            this.mHostListener.updateTabIndicator(0);
        } else {
            this.mHostListener.updateTabIndicator(1);
        }
        if (immediate) {
            computeScroll();
        }
        this.mForceScreenScrolled = true;
        invalidate();
        return Math.abs(delta) > 0;
    }

    public void setAfwTabPositon() {
        if (this.mMenuItemFactory.getManagedProfileStartPage() == -1 || getCurrentPage() < this.mMenuItemFactory.getManagedProfileStartPage()) {
            this.mHostListener.updateTabIndicator(0);
        } else {
            this.mHostListener.updateTabIndicator(1);
        }
    }

    public int validatePageIndexForLoop(int index) {
        if (getChildCount() <= 1) {
            return index;
        }
        int iIndexOfHead = indexOfHead();
        int iIndexOfTail = indexOfTail();
        return (index < iIndexOfHead || iIndexOfTail < index) ? index : (isHeadToTail() && isTailPage(index)) ? iIndexOfHead - 1 : (isTailToHead() && isHeadPage(index)) ? iIndexOfTail + 1 : index;
    }

    public int getScrollForPageLoop(int index) {
        int iValidatePageIndexForLoop = validatePageIndexForLoop(index);
        int iIndexOfHead = indexOfHead();
        int iIndexOfTail = indexOfTail();
        int childMeasuredWidth = getChildMeasuredWidth(iIndexOfHead);
        int childMeasuredWidth2 = getChildMeasuredWidth(iIndexOfTail);
        int i = !this.mIsRtl ? -1 : 1;
        int pageSpacing = getPageSpacing();
        this.mPrevHeadPageScroll = getScrollForPage(iIndexOfHead) + ((childMeasuredWidth + pageSpacing) * i);
        this.mNextTailPageScroll = getScrollForPage(iIndexOfTail) + (i * (-1) * (childMeasuredWidth2 + pageSpacing));
        if (iValidatePageIndexForLoop < indexOfHead()) {
            return this.mPrevHeadPageScroll;
        }
        if (indexOfTail() < iValidatePageIndexForLoop) {
            return this.mNextTailPageScroll;
        }
        return getScrollForPage(iValidatePageIndexForLoop);
    }

    private void updateScrollProgress(int currentPage, int scrollX) {
        int[] scrollDeltaAndRange = getScrollDeltaAndRange(validatePageIndexForLoop(indexOfChild(getChildAt(currentPage))), scrollX);
        int i = scrollDeltaAndRange[0];
        int iAbs = Math.abs(scrollDeltaAndRange[1]);
        this.scrollProgress = 0.0f;
        if (iAbs != 0) {
            float f = i / iAbs;
            this.scrollProgress = f;
            if (f < 0.0f) {
                this.scrollProgress = f + 1.0f;
            }
        }
    }

    public int[] getScrollDeltaAndRange(int index, int scrollX) {
        int scrollForPageLoop = scrollX - getScrollForPageLoop(index);
        int i = index + 1;
        if ((scrollForPageLoop < 0 && !this.mIsRtl) || (scrollForPageLoop > 0 && this.mIsRtl)) {
            i = index - 1;
        }
        return new int[]{scrollForPageLoop, getScrollForPageLoop(i) - getScrollForPageLoop(index)};
    }

    public boolean isOverscrollLeft() {
        return this.overscrollState == ScreenEffectConst.OverscrollState.OVERSCROLL_LEFT;
    }

    public boolean isOverscrollRight() {
        return this.overscrollState == ScreenEffectConst.OverscrollState.OVERSCROLL_RIGHT;
    }

    public boolean isHeadToTail() {
        return !this.mIsRtl ? isOverscrollLeft() : isOverscrollRight();
    }

    public boolean isTailToHead() {
        return !this.mIsRtl ? isOverscrollRight() : isOverscrollLeft();
    }

    public int getPageNearestToCenterOfScreenForLoop(PagedView pagedView) {
        int iIndexOfTail;
        int viewportOffsetX = pagedView.getViewportOffsetX();
        int scrollX = pagedView.getScrollX() + viewportOffsetX + (pagedView.getViewportWidth() / 2);
        int childCount = pagedView.getChildCount();
        int i = Integer.MAX_VALUE;
        int i2 = -1;
        for (int i3 = 0; i3 < childCount; i3++) {
            View pageAt = pagedView.getPageAt(i3);
            boolean zIsHeadPage = isHeadPage(i3);
            boolean zIsTailPage = isTailPage(i3);
            int measuredWidth = pageAt.getMeasuredWidth() / 2;
            int childOffset = pagedView.getChildOffset(i3) + viewportOffsetX + measuredWidth;
            if (isOverscrollLeft() && (this.mIsRtl ? zIsHeadPage : zIsTailPage)) {
                childOffset = viewportOffsetX - measuredWidth;
            } else if (isOverscrollRight() && (this.mIsRtl ? zIsTailPage : zIsHeadPage)) {
                childOffset = this.mMaxScrollForLoop + viewportOffsetX + measuredWidth;
            }
            int iAbs = Math.abs(childOffset - scrollX);
            if (iAbs < i) {
                if (isHeadToTail() && zIsTailPage) {
                    iIndexOfTail = indexOfHead() - 1;
                } else if (isTailToHead() && zIsHeadPage) {
                    iIndexOfTail = indexOfTail() + 1;
                } else {
                    i2 = i3;
                    i = iAbs;
                }
                i2 = iIndexOfTail;
                i = iAbs;
            }
        }
        return i2;
    }

    @Override // com.lge.launcher3.PagedView
    public int getPageNearestToCenterOfScreen() {
        if (isEnableLoop()) {
            return getPageNearestToCenterOfScreenForLoop(this);
        }
        return super.getPageNearestToCenterOfScreen();
    }

    @Override // com.lge.launcher3.PagedView
    public boolean snapToPageWithVelocity(int whichPage, int velocity) {
        int scrollForPageLoop;
        if (this.mWasInOverscroll && !isEnableLoop()) {
            snapToDestination();
        }
        int iValidateNewPage = validateNewPage(whichPage);
        int viewportWidth = getViewportWidth() / 2;
        if (!isEnableLoop()) {
            scrollForPageLoop = getScrollForPage(iValidateNewPage);
        } else {
            scrollForPageLoop = getScrollForPageLoop(iValidateNewPage);
        }
        int i = scrollForPageLoop - this.mUnboundedScrollX;
        if (Math.abs(velocity) < this.mMinFlingVelocity) {
            return snapToPage(iValidateNewPage, PAGE_SNAP_ANIMATION_DURATION);
        }
        float fMin = Math.min(1.0f, (Math.abs(i) * 1.0f) / (viewportWidth * 2));
        float f = viewportWidth;
        float fDistanceInfluenceForSnapDuration = f + (distanceInfluenceForSnapDuration(fMin) * f);
        ScreenEffectManager screenEffectManager = ScreenEffectManager.getInstance(getContext());
        int iMax = Math.max(screenEffectManager.adjustMinSnapVelocity(this.mDefaultInterpolator, 1500), Math.abs(velocity));
        int iAdjustSnapDuration = screenEffectManager.adjustSnapDuration(this.mDefaultInterpolator, Math.round(Math.abs(fDistanceInfluenceForSnapDuration / iMax) * 1000.0f) * 4);
        screenEffectManager.updateInterpolatorTension(this.mDefaultInterpolator, iMax, iAdjustSnapDuration);
        screenEffectManager.enableToSwitchInterpolator();
        return snapToPage(iValidateNewPage, i, iAdjustSnapDuration);
    }

    public void updateUninstallPolicytoAll() {
        mapOverItems(true, new Workspace.ItemOperator() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.23
            @Override // com.android.launcher3.Workspace.ItemOperator
            public boolean evaluate(ItemInfo info, View v, View parent) {
                if ((!(info instanceof AllAppsItemInfo) && !(info instanceof ShortcutInfo)) || !(v instanceof BubbleTextView)) {
                    return false;
                }
                UninstallModeManager.getInstance(AllAppsPagedView.this.getContext()).setUninstallTypeForBadgeView((BubbleTextView) v);
                return false;
            }
        });
    }

    public void updateDataFreetoAll() {
        mapOverItems(true, new Workspace.ItemOperator() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.24
            @Override // com.android.launcher3.Workspace.ItemOperator
            public boolean evaluate(ItemInfo info, View v, View parent) {
                if (!(v instanceof BubbleTextView)) {
                    return false;
                }
                ((BubbleTextView) v).invalidateDataFreeBadge();
                return false;
            }
        });
    }

    public void updateUninstallPolicy(final ArrayList<String> packageList) {
        mapOverItems(true, new Workspace.ItemOperator() { // from class: com.lge.launcher3.allapps.AllAppsPagedView.25
            @Override // com.android.launcher3.Workspace.ItemOperator
            public boolean evaluate(ItemInfo info, View v, View parent) {
                ComponentName targetComponent;
                if ((!(info instanceof AllAppsItemInfo) && !(info instanceof ShortcutInfo)) || !(v instanceof BubbleTextView) || (targetComponent = info.getTargetComponent()) == null || !packageList.contains(targetComponent.getPackageName())) {
                    return false;
                }
                UninstallModeManager.getInstance(AllAppsPagedView.this.getContext()).setUninstallTypeForBadgeView((BubbleTextView) v);
                return false;
            }
        });
    }

    @Override // com.lge.launcher3.PagedView
    protected void determineScrollingStart(MotionEvent ev, float touchSlopScale) {
        if (!isInArrangeMode()) {
            touchSlopScale = sTouchSlopRatio;
        }
        super.determineScrollingStart(ev, touchSlopScale);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == 0) {
            setBackgroundTransparentOfFocusHandler(false);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override // com.lge.launcher3.PagedView, android.view.View
    public boolean onGenericMotionEvent(MotionEvent event) {
        setBackgroundTransparentOfFocusHandler(true);
        return super.onGenericMotionEvent(event);
    }

    @Override // com.lge.launcher3.PagedView
    protected void setBackgroundTransparentOfFocusHandler(boolean useTransparentColor) {
        FocusIndicatorView focusIndicatorView = this.mFocusIndicatorView;
        if (focusIndicatorView != null) {
            focusIndicatorView.setBackgroundTransparent(useTransparentColor);
        }
    }

    @Override // com.lge.launcher3.PagedView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getPointerCount() > 1) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override // android.view.View
    public void cancelLongPress() {
        super.cancelLongPress();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null) {
                childAt.cancelLongPress();
            }
        }
    }
}
