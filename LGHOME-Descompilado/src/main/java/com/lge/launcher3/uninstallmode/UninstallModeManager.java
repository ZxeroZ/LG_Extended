package com.lge.launcher3.uninstallmode;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.view.View;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeleteDropTarget;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Workspace;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.folder.FolderPagedView;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsPagedView;
import com.lge.launcher3.badge.uninstall.IUninstallBadgeView;
import com.lge.launcher3.badge.uninstall.UninstallBadgeUtils;
import com.lge.launcher3.droptarget.ButtonDropTargetUtils;
import com.lge.launcher3.droptarget.DisableDropTarget;
import com.lge.launcher3.droptarget.LGUninstallDropTarget;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUserLog;
import com.lge.launcher3.util.ManagedProfileUtils;
import com.lge.launcher3.util.TalkBackUtils;
import com.lge.launcher3.util.Utilities;
import com.lge.launcher3.util.VibratorManager;
import com.lge.launcher3.util.WindowUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class UninstallModeManager {
    public static final boolean DEBUG = false;
    public static boolean DEBUG_RESTRICT_PACKAGE = false;
    private static final String SETTING_PACKAGE_NAME = "com.android.settings";
    private static final String SETTING_PHONE_ADMIN_ACTIVITY_NAME = "com.android.settings.DeviceAdminSettings";
    public static final String TAG = "UninstallModeManager";
    private static UninstallModeManager sInstance;
    private Context mContext;
    private boolean mIsPowerSaveMode;
    private ValueAnimator mValueAnimator;
    private AsyncTask<Void, Void, Void> mUpdateUninstallTypeAsyncTask = null;
    ArrayList<IUninstallBadgeView> mUninstallableItemArrayList = null;
    private boolean mIsBindingFinished = false;
    private Rect mRectOfBadgeView = new Rect();
    private int mDisplayWidth = 0;
    private boolean mIsFolderOpen = false;
    private HashSet<String> mRestrictPackages = new HashSet<>();
    private HashSet<String> mAdminPackages = new HashSet<>();

    public void updateRestrictPackages() {
    }

    public static UninstallModeManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new UninstallModeManager(context.getApplicationContext());
        }
        return sInstance;
    }

    private UninstallModeManager(Context context) {
        this.mContext = null;
        LGLog.i(TAG, "Create a new UninstallModeManager instance.");
        this.mContext = context;
    }

    public void onBindingFinished(Workspace workspace) {
        if (isDisabled()) {
            return;
        }
        this.mIsBindingFinished = true;
        setUninstallTypeForAllBadgeViews(workspace);
    }

    public boolean isPackageOfRestrictPackageList(String packageName) {
        if (DEBUG_RESTRICT_PACKAGE) {
            LGLog.i(TAG, "isPackageOfRestrictPackageList() start. packageName = " + packageName);
        }
        boolean z = false;
        synchronized (this.mRestrictPackages) {
            if (this.mRestrictPackages.contains(packageName)) {
                z = true;
                LGLog.i(TAG, "This package is restricted. packageName = " + packageName);
            }
        }
        if (DEBUG_RESTRICT_PACKAGE) {
            LGLog.i(TAG, "isPackageOfRestrictPackageList() end. packageName = " + packageName + ", result = " + z);
        }
        return z;
    }

    public void makeRestrictPackages() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (DEBUG_RESTRICT_PACKAGE) {
            LGLog.i(TAG, "makeRestrictPackages() : Start");
        }
        PackageManager packageManager = this.mContext.getPackageManager();
        this.mRestrictPackages.clear();
        this.mAdminPackages.clear();
        this.mAdminPackages.addAll(ManagedProfileUtils.getAdminPackageList(this.mContext));
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(8192);
        for (PackageInfo packageInfo : installedPackages) {
            if (packageInfo.packageName != null && isRestrictPackage(packageInfo.packageName)) {
                this.mRestrictPackages.add(packageInfo.packageName);
            }
        }
        long jCurrentTimeMillis2 = System.currentTimeMillis() - jCurrentTimeMillis;
        if (DEBUG_RESTRICT_PACKAGE) {
            LGLog.i(TAG, "makeRestrictPackages() : end. size = " + this.mRestrictPackages.size() + ", elaspeTime = " + jCurrentTimeMillis2 + ", install package = " + installedPackages.size());
        }
        installedPackages.clear();
    }

    public boolean isRestrictPackage(ComponentName componentName, int type) {
        if (DEBUG_RESTRICT_PACKAGE) {
            LGLog.i(TAG, "isRestrictPackage() start. packageName = " + componentName);
        }
        boolean z = true;
        if (componentName == null || (type != 2 ? !(ManagedProfileUtils.isLGMDMAppNotAllowUninstall(this.mContext, componentName.getPackageName()) || ManagedProfileUtils.hasUserRestriction(this.mContext) || ManagedProfileUtils.isUninstallBlocked(this.mContext, componentName.getPackageName())) : !(ManagedProfileUtils.isAdminApplication(this.mContext, componentName) || ManagedProfileUtils.isLGMDMAppNotAllowUninstall(this.mContext, componentName.getPackageName()) || ManagedProfileUtils.hasUserRestriction(this.mContext) || ManagedProfileUtils.isUninstallBlocked(this.mContext, componentName.getPackageName())))) {
            z = false;
        }
        if (DEBUG_RESTRICT_PACKAGE) {
            LGLog.i(TAG, "isRestrictPackage() end. packageName = " + componentName + ", result = " + z);
        }
        return z;
    }

    public boolean isRestrictPackage(String packageName) {
        if (DEBUG_RESTRICT_PACKAGE) {
            LGLog.i(TAG, "isRestrictPackage() start. packageName = " + packageName);
        }
        boolean z = packageName != null && (this.mAdminPackages.contains(packageName) || ManagedProfileUtils.isLGMDMAppNotAllowUninstall(this.mContext, packageName) || ManagedProfileUtils.hasUserRestriction(this.mContext) || ManagedProfileUtils.isUninstallBlocked(this.mContext, packageName));
        if (DEBUG_RESTRICT_PACKAGE) {
            LGLog.i(TAG, "isRestrictPackage() end. packageName = " + packageName + ", result = " + z);
        }
        return z;
    }

    private void setUninstallTypeForAllBadgeViews(final Workspace workspace) {
        if (workspace == null) {
            return;
        }
        cancelUpdateUninstallTypeAsyncTask();
        final HashSet<View> allItemsList = LGHomeFeature.isEnableDefaultHome() ? getAllItemsList(workspace) : getAllItemsListIncludeFolderIcon(workspace);
        if (allItemsList == null || allItemsList.isEmpty()) {
            LGLog.i(TAG, String.format("setUninstallTypeForAllBadgeViews() : allItemsList(%s)", allItemsList));
            return;
        }
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() { // from class: com.lge.launcher3.uninstallmode.UninstallModeManager.1
            /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
            /* JADX DEBUG: Multi-variable search result rejected for r0v4, resolved type: android.view.View */
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Multi-variable type inference failed */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... params) {
                Object tag;
                LGLog.i(UninstallModeManager.TAG, "setUninstallTypeForAllBadgeViews() : AsyncTask - Start");
                for (View view : allItemsList) {
                    if (view != 0 && (view instanceof IUninstallBadgeView) && (tag = view.getTag()) != null && (tag instanceof ItemInfo)) {
                        UninstallBadgeUtils.UninstallType uninstallType = UninstallModeManager.this.getUninstallType((ItemInfo) tag);
                        if (uninstallType != null) {
                            ((IUninstallBadgeView) view).setUninstallType(uninstallType);
                        }
                    }
                }
                LGLog.i(UninstallModeManager.TAG, "setUninstallTypeForAllBadgeViews() : AsyncTask - End");
                return null;
            }
        };
        this.mUpdateUninstallTypeAsyncTask = asyncTask;
        asyncTask.execute(new Void[0]);
    }

    public boolean setUninstallTypeForBadgeView(View item) {
        if (isDisabled()) {
            return false;
        }
        if (item == 0) {
            LGLog.i(TAG, String.format("addToUninstallableItemList() : item is null.", item));
            return false;
        }
        if (!this.mIsBindingFinished) {
            return false;
        }
        Object tag = item.getTag();
        if (tag == null || !(tag instanceof ItemInfo)) {
            LGLog.i(TAG, String.format("addToUninstallableItemList() : tag is null or not ItemInfo.", item));
            return false;
        }
        ItemInfo itemInfo = (ItemInfo) tag;
        UninstallBadgeUtils.UninstallType uninstallType = getUninstallType(itemInfo);
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            uninstallType = UninstallBadgeUtils.UninstallType.DELETE;
        }
        IUninstallBadgeView iUninstallBadgeView = (IUninstallBadgeView) item;
        iUninstallBadgeView.setUninstallType(uninstallType);
        if (!iUninstallBadgeView.isUninstallable()) {
            return false;
        }
        LGLog.i(TAG, String.format("addToUninstallableItemList() : An itemType [%d]'s uninstall type is set.", Integer.valueOf(itemInfo.itemType)));
        if (isInUninstallMode() && !this.mUninstallableItemArrayList.contains(item)) {
            iUninstallBadgeView.setVisibilityForUninstallBadge(true, 0);
            if (!this.mUninstallableItemArrayList.contains(iUninstallBadgeView)) {
                this.mUninstallableItemArrayList.add(iUninstallBadgeView);
            }
        }
        return true;
    }

    public UninstallBadgeUtils.UninstallType getUninstallType(ItemInfo itemInfo) {
        if (DisableDropTarget.supportsDrop(this.mContext, itemInfo)) {
            return UninstallBadgeUtils.UninstallType.DISABLE;
        }
        if (LGUninstallDropTarget.supportsDrop(this.mContext, itemInfo)) {
            return UninstallBadgeUtils.UninstallType.UNINSTALL;
        }
        if (DeleteDropTarget.supportsDrop(itemInfo) || ButtonDropTargetUtils.isShortcutWithApplicationType(this.mContext, itemInfo)) {
            return UninstallBadgeUtils.UninstallType.DELETE;
        }
        return null;
    }

    private HashSet<View> getAllItemsList(Workspace workspace) {
        final HashSet<View> hashSet = new HashSet<>();
        workspace.mapOverItems(true, new Workspace.ItemOperator() { // from class: com.lge.launcher3.uninstallmode.UninstallModeManager.2
            @Override // com.android.launcher3.Workspace.ItemOperator
            public boolean evaluate(ItemInfo info, View v, View parent) {
                hashSet.add(v);
                return false;
            }
        });
        return hashSet;
    }

    private HashSet<View> getAllItemsListIncludeFolderIcon(Workspace workspace) {
        final HashSet<View> hashSet = new HashSet<>();
        workspace.mapOverItemsIncludeFolderIcon(true, new Workspace.ItemOperator() { // from class: com.lge.launcher3.uninstallmode.UninstallModeManager.3
            @Override // com.android.launcher3.Workspace.ItemOperator
            public boolean evaluate(ItemInfo info, View v, View parent) {
                hashSet.add(v);
                return false;
            }
        });
        return hashSet;
    }

    public boolean checkToEnterUninstallMode(Launcher launcher, DragSource dragSource, DropTarget dropTarget, ItemInfo oldItemInfo, ItemInfo newItemInfo) {
        if (isDisabled() || launcher == null || dragSource == null || dropTarget == null || oldItemInfo == null || newItemInfo == null) {
            return false;
        }
        boolean z = dragSource == dropTarget && oldItemInfo.container == newItemInfo.container && oldItemInfo.cellX == newItemInfo.cellX && oldItemInfo.cellY == newItemInfo.cellY && !launcher.isSafeMode();
        boolean z2 = dragSource instanceof Workspace;
        if (z2 && z2) {
            return z && oldItemInfo.screenId == newItemInfo.screenId;
        }
        boolean z3 = dragSource instanceof Folder;
        return z3 && z3 && z && oldItemInfo.rank == newItemInfo.rank;
    }

    public void enterUninstallMode(Launcher launcher) {
        if (isDisabled() || isInUninstallMode()) {
            LGLog.d(TAG, "enterUninstallMode can't start(" + isDisabled() + ", " + isInUninstallMode() + ")");
            return;
        }
        if (launcher == null) {
            return;
        }
        Workspace workspace = launcher.getWorkspace();
        if (workspace == null) {
            LGLog.i(TAG, "workspace is null.");
            return;
        }
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && launcher.getCarouselLayout() != null) {
            launcher.getCarouselLayout().getAdapter().notifyDataSetChanged();
        }
        LGLog.i(TAG, "enterUninstallMode()");
        this.mIsPowerSaveMode = Utilities.isPowerSaveMode(this.mContext);
        workspace.showAllCrossHair(true);
        setupAllUninstallableItemsList(workspace);
        invalidateUninstallBadgeView(true);
        runUninstallBadgeAnimation(true, 0);
        LGUserLog.send(launcher, LGUserLog.FEATURENAME_UNINSTALLMODE);
        sendAccessibilityForUninstallMode(launcher);
        workspace.updateAccessibilityFlags();
        launcher.getRootView().setDisallowBackGesture(false);
    }

    private void sendAccessibilityForUninstallMode(Launcher launcher) {
        Resources resources = launcher.getResources();
        TalkBackUtils.sendAccessibilityEvent(this.mContext, resources.getString(R.string.sp_homescreen_category_NORMAL) + "," + resources.getString(R.string.sp_editing_NORMAL), true);
    }

    public void exitUninstallMode(Launcher launcher) {
        if (isDisabled() || !isInUninstallMode()) {
            LGLog.d(TAG, "exitUninstallMode can't start(" + isDisabled() + ", " + isInUninstallMode() + ")");
            return;
        }
        if (launcher == null) {
            return;
        }
        Workspace workspace = launcher.getWorkspace();
        if (workspace == null) {
            LGLog.i(TAG, "workspace is null.");
            return;
        }
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && launcher.getCarouselLayout() != null) {
            launcher.getCarouselLayout().getAdapter().notifyDataSetChanged();
            launcher.getCarouselLayout().getSwivelWeatherView().setVisibility(0);
        }
        LGLog.i(TAG, "exitUninstallMode()");
        launcher.closeFolder(false);
        runUninstallBadgeAnimation(false, 0);
        setVisibilityForAllUninstallBadges(workspace, false);
        clearUninstallableItemList();
        if (launcher.isInState(LauncherState.DYNAMIC_GRID_OVERVIEW)) {
            workspace.showAllCrossHair(false);
        } else {
            launcher.getRootView().setDisallowBackGesture(true);
        }
    }

    private void setVisibilityForAllUninstallBadges(Workspace workspace, boolean visible) {
        ArrayList<IUninstallBadgeView> arrayList;
        if (workspace == null || (arrayList = this.mUninstallableItemArrayList) == null || arrayList.isEmpty()) {
            return;
        }
        for (IUninstallBadgeView iUninstallBadgeView : this.mUninstallableItemArrayList) {
            if (iUninstallBadgeView != null) {
                iUninstallBadgeView.setVisibilityForUninstallBadge(visible, 0);
            }
        }
    }

    private void setupAllUninstallableItemsList(Workspace workspace) {
        clearUninstallableItemList();
        this.mUninstallableItemArrayList = new ArrayList<>();
        LGLog.d(TAG, "UninstallableItemsList is created");
        if (LGHomeFeature.isEnableDefaultHome()) {
            workspace.mapOverItems(true, new Workspace.ItemOperator() { // from class: com.lge.launcher3.uninstallmode.UninstallModeManager.4
                /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.view.View */
                /* JADX WARN: Multi-variable type inference failed */
                @Override // com.android.launcher3.Workspace.ItemOperator
                public boolean evaluate(ItemInfo info, View v, View parent) {
                    if (v != 0 && (v instanceof IUninstallBadgeView)) {
                        IUninstallBadgeView iUninstallBadgeView = (IUninstallBadgeView) v;
                        if (iUninstallBadgeView.isUninstallable() && !UninstallModeManager.this.mUninstallableItemArrayList.contains(iUninstallBadgeView)) {
                            UninstallModeManager.this.mUninstallableItemArrayList.add(iUninstallBadgeView);
                        }
                    }
                    return false;
                }
            });
        } else {
            workspace.mapOverItemsIncludeFolderIcon(true, new Workspace.ItemOperator() { // from class: com.lge.launcher3.uninstallmode.UninstallModeManager.5
                /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.view.View */
                /* JADX WARN: Multi-variable type inference failed */
                @Override // com.android.launcher3.Workspace.ItemOperator
                public boolean evaluate(ItemInfo info, View v, View parent) {
                    if (v != 0 && (v instanceof IUninstallBadgeView)) {
                        IUninstallBadgeView iUninstallBadgeView = (IUninstallBadgeView) v;
                        if (iUninstallBadgeView.isUninstallable() && !UninstallModeManager.this.mUninstallableItemArrayList.contains(iUninstallBadgeView)) {
                            UninstallModeManager.this.mUninstallableItemArrayList.add(iUninstallBadgeView);
                        }
                    }
                    return false;
                }
            });
        }
    }

    private void clearUninstallableItemList() {
        ArrayList<IUninstallBadgeView> arrayList = this.mUninstallableItemArrayList;
        if (arrayList != null) {
            arrayList.clear();
            this.mUninstallableItemArrayList = null;
        }
    }

    public boolean isInUninstallMode() {
        return this.mUninstallableItemArrayList != null;
    }

    public static boolean isDisabled() {
        return !isEnabled();
    }

    public static boolean isEnabled() {
        return LGHomeFeature.Config.FEATURE_SUPPORT_UNINSTALL_MODE.getValue();
    }

    /* JADX DEBUG: Multi-variable search result rejected for r8v0, resolved type: android.view.View */
    /* JADX WARN: Multi-variable type inference failed */
    public boolean checkAndShowUninstallPopup(Launcher launcher, View view) {
        Object tag;
        if (view == 0 || !isInUninstallMode() || (tag = view.getTag()) == null) {
            return false;
        }
        ItemInfo itemInfo = (ItemInfo) tag;
        String str = TAG;
        LGLog.i(str, String.format("checkAndShowUninstallPopup() : Skip to click an item.[%s]", tag));
        if (!(view instanceof IUninstallBadgeView)) {
            return true;
        }
        IUninstallBadgeView iUninstallBadgeView = (IUninstallBadgeView) view;
        if (TalkBackUtils.isEnabled(this.mContext)) {
            if (!iUninstallBadgeView.hasUnistallBadge()) {
                LGLog.d(str, "Has not Unistall badge ");
                return true;
            }
        } else if (!iUninstallBadgeView.isUninstallable() || !iUninstallBadgeView.isTouchedUninstallBadge()) {
            return LGHomeFeature.isEnableDefaultHome() || !(view instanceof FolderIcon);
        }
        LGUserLog.send(launcher, LGUserLog.FEATURENAME_REMOVE_ITEM_BY_UNINSTALLMODE);
        UninstallBadgeUtils.UninstallType uninstallType = iUninstallBadgeView.getUninstallType();
        Workspace workspace = launcher.getWorkspace();
        if (!LGHomeFeature.isEnableDefaultHome() && workspace != null && Workspace.State.OVERVIEW.equals(workspace.getState())) {
            uninstallType = UninstallBadgeUtils.UninstallType.DELETE;
        }
        if (ManagedProfileUtils.isAdminApplication(this.mContext, itemInfo.getTargetComponent())) {
            new AlertDialog.Builder(view.getContext()).setMessage(R.string.sp_delete_admin_app).setPositiveButton(R.string.settings_button_text, new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.uninstallmode.UninstallModeManager.6
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent();
                    intent.addFlags(335544320);
                    intent.setClassName(UninstallModeManager.SETTING_PACKAGE_NAME, UninstallModeManager.SETTING_PHONE_ADMIN_ACTIVITY_NAME);
                    UninstallModeManager.this.mContext.startActivity(intent);
                }
            }).setNegativeButton(R.string.droptarget_cancel, (DialogInterface.OnClickListener) null).create().show();
            return true;
        }
        int i = AnonymousClass13.$SwitchMap$com$lge$launcher3$badge$uninstall$UninstallBadgeUtils$UninstallType[uninstallType.ordinal()];
        if (i == 1) {
            launcher.mSuppressCloseFolder = true;
            LGUninstallDropTarget.startUninstallActivity(launcher, itemInfo);
        } else if (i == 2) {
            DeleteItemDialog.showDialogFragment(launcher, itemInfo, view, null);
        } else if (i == 3) {
            launcher.mSuppressCloseFolder = true;
            DisableDropTarget.startDisableActivity(launcher, itemInfo);
        }
        return true;
    }

    /* JADX INFO: renamed from: com.lge.launcher3.uninstallmode.UninstallModeManager$13, reason: invalid class name */
    static /* synthetic */ class AnonymousClass13 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$badge$uninstall$UninstallBadgeUtils$UninstallType;

        static {
            int[] iArr = new int[UninstallBadgeUtils.UninstallType.values().length];
            $SwitchMap$com$lge$launcher3$badge$uninstall$UninstallBadgeUtils$UninstallType = iArr;
            try {
                iArr[UninstallBadgeUtils.UninstallType.UNINSTALL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$badge$uninstall$UninstallBadgeUtils$UninstallType[UninstallBadgeUtils.UninstallType.DELETE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$lge$launcher3$badge$uninstall$UninstallBadgeUtils$UninstallType[UninstallBadgeUtils.UninstallType.DISABLE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    public void removeFolderItem(Workspace workspace, ItemInfo itemInfo, View view) {
        if (workspace == null || itemInfo == null || view == null || !(itemInfo instanceof ShortcutInfo)) {
            return;
        }
        final ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
        workspace.mapOverItems(true, new Workspace.ItemOperator() { // from class: com.lge.launcher3.uninstallmode.UninstallModeManager.7
            @Override // com.android.launcher3.Workspace.ItemOperator
            public boolean evaluate(ItemInfo info, View view2, View parent) {
                FolderInfo folderInfo;
                if (!(parent instanceof FolderIcon) || (folderInfo = ((FolderIcon) parent).getFolderInfo()) == null || !info.equals(shortcutInfo)) {
                    return false;
                }
                folderInfo.remove(shortcutInfo);
                return true;
            }
        });
    }

    public float getShirinkFactor(Workspace workspace) {
        if (isDisabled() || workspace == null || !isInUninstallMode()) {
            return 1.0f;
        }
        return workspace.getSpringLoadedShrinkFactor();
    }

    private void cancelUpdateUninstallTypeAsyncTask() {
        AsyncTask<Void, Void, Void> asyncTask = this.mUpdateUninstallTypeAsyncTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.mUpdateUninstallTypeAsyncTask = null;
        }
    }

    public void destroy() {
        LGLog.i(TAG, "Destroy the UninstallModeManager instance.");
        clearUninstallableItemList();
        cancelUpdateUninstallTypeAsyncTask();
        this.mContext = null;
        sInstance = null;
        runUninstallBadgeAnimation(false, 0);
        this.mDisplayWidth = 0;
        UninstallBadgeUtils.initUninstallBadge();
    }

    private void setUninstallBadgeAnimation() {
        ValueAnimator valueAnimatorOfInt = ValueAnimator.ofInt(0, UninstallBadgeUtils.sDefaultRangeOfUninstallBadge);
        this.mValueAnimator = valueAnimatorOfInt;
        valueAnimatorOfInt.setDuration(600L);
        this.mValueAnimator.setRepeatCount(-1);
        this.mValueAnimator.setRepeatMode(2);
        this.mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.launcher3.uninstallmode.UninstallModeManager.8
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                if (UninstallBadgeUtils.setRangeOfUninstallBadge(Integer.parseInt(animation.getAnimatedValue().toString()))) {
                    UninstallModeManager.this.invalidateUninstallBadgeView(false);
                }
            }
        });
        this.mValueAnimator.addListener(new Animator.AnimatorListener() { // from class: com.lge.launcher3.uninstallmode.UninstallModeManager.9
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animation) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                UninstallBadgeUtils.setRangeOfUninstallBadge(0);
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                UninstallBadgeUtils.setRangeOfUninstallBadge(0);
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                UninstallBadgeUtils.setRangeOfUninstallBadge(0);
                UninstallModeManager.this.invalidateUninstallBadgeView(false);
            }
        });
    }

    public void runUninstallBadgeAnimation(boolean visible, int delay) {
        if (visible) {
            if (this.mDisplayWidth == 0) {
                this.mDisplayWidth = WindowUtils.getDisplayWidth(this.mContext);
            }
            if (this.mValueAnimator == null) {
                setUninstallBadgeAnimation();
            }
            this.mValueAnimator.setStartDelay(delay);
            this.mValueAnimator.start();
            return;
        }
        ValueAnimator valueAnimator = this.mValueAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.mValueAnimator.removeAllUpdateListeners();
            this.mValueAnimator.removeAllListeners();
            this.mValueAnimator = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidateUninstallBadgeView(boolean first) {
        ArrayList<IUninstallBadgeView> arrayList = this.mUninstallableItemArrayList;
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        for (int i = 0; i < this.mUninstallableItemArrayList.size(); i++) {
            if (this.mUninstallableItemArrayList.get(i) != null) {
                this.mUninstallableItemArrayList.get(i).getGlobalVisibleRectForBadge(this.mRectOfBadgeView);
                if (first) {
                    if (this.mRectOfBadgeView.left <= 0 || this.mRectOfBadgeView.left >= this.mDisplayWidth) {
                        this.mUninstallableItemArrayList.get(i).setVisibilityForUninstallBadge(true, 1);
                    }
                } else if (this.mRectOfBadgeView.left > 0 && this.mRectOfBadgeView.left < this.mDisplayWidth) {
                    if (this.mIsFolderOpen) {
                        if (this.mUninstallableItemArrayList.get(i).isInFolder()) {
                            this.mUninstallableItemArrayList.get(i).invalidateUninstallBadge(true, true);
                        }
                    } else {
                        this.mUninstallableItemArrayList.get(i).invalidateUninstallBadge(true, true);
                    }
                }
            }
        }
    }

    public void setFolderOpen(boolean isOpen) {
        this.mIsFolderOpen = isOpen;
    }

    public void setUninstallTypeForAllBadgeViews(final AllAppsPagedView appsPagedView) {
        if (appsPagedView == null) {
            return;
        }
        cancelUpdateUninstallTypeAsyncTask();
        final HashSet<View> allItemsList = getAllItemsList(appsPagedView);
        if (allItemsList == null || allItemsList.isEmpty()) {
            LGLog.i(TAG, String.format("setUninstallTypeForAllBadgeViews() : allItemsList(%s)", allItemsList));
            return;
        }
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() { // from class: com.lge.launcher3.uninstallmode.UninstallModeManager.10
            /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
            /* JADX DEBUG: Multi-variable search result rejected for r0v4, resolved type: android.view.View */
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Multi-variable type inference failed */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... params) {
                Object tag;
                LGLog.i(UninstallModeManager.TAG, "setUninstallTypeForAllBadgeViews() : AsyncTask - Start");
                for (View view : allItemsList) {
                    if (view != 0 && (view instanceof IUninstallBadgeView) && (tag = view.getTag()) != null && (tag instanceof ItemInfo)) {
                        UninstallBadgeUtils.UninstallType uninstallType = UninstallModeManager.this.getUninstallType((ItemInfo) tag);
                        if (uninstallType != null) {
                            ((IUninstallBadgeView) view).setUninstallType(uninstallType);
                        }
                    }
                }
                LGLog.i(UninstallModeManager.TAG, "setUninstallTypeForAllBadgeViews() : AsyncTask - End");
                return null;
            }
        };
        this.mUpdateUninstallTypeAsyncTask = asyncTask;
        asyncTask.execute(new Void[0]);
    }

    private HashSet<View> getAllItemsList(AllAppsPagedView appsPagedView) {
        final HashSet<View> hashSet = new HashSet<>();
        appsPagedView.mapOverItems(true, new Workspace.ItemOperator() { // from class: com.lge.launcher3.uninstallmode.UninstallModeManager.11
            @Override // com.android.launcher3.Workspace.ItemOperator
            public boolean evaluate(ItemInfo info, View v, View parent) {
                hashSet.add(v);
                return false;
            }
        });
        return hashSet;
    }

    public void enterUninstallMode(AllAppsPagedView allApps) {
        if (isDisabled() || isInUninstallMode()) {
            return;
        }
        if (allApps == null) {
            LGLog.i(TAG, "AllAppsHost is null.");
            return;
        }
        LGLog.i(TAG, "enterUninstallMode(allapps)");
        this.mIsPowerSaveMode = Utilities.isPowerSaveMode(this.mContext);
        setupAllUninstallableItemsList(allApps);
        invalidateUninstallBadgeView(true);
        runUninstallBadgeAnimation(true, 0);
        LGUserLog.send(allApps.getContext(), LGUserLog.FEATURENAME_UNINSTALLMODE);
        VibratorManager.performHapticFeedback(allApps.getContext(), 0);
        sendAccessibilityForUninstallMode(allApps);
    }

    private void sendAccessibilityForUninstallMode(AllAppsPagedView allApps) {
        Resources resources = allApps.getResources();
        TalkBackUtils.sendAccessibilityEvent(this.mContext, resources.getString(R.string.sp_homescreen_category_NORMAL) + "," + resources.getString(R.string.sp_editing_NORMAL), true);
    }

    public void exitUninstallMode(AllAppsPagedView allApps) {
        if (isDisabled() || !isInUninstallMode()) {
            return;
        }
        if (allApps == null) {
            LGLog.i(TAG, "allApps is null.");
            return;
        }
        LGLog.i(TAG, "exitUninstallMode()");
        allApps.closeFolder(false, true);
        runUninstallBadgeAnimation(false, 0);
        setVisibilityForAllUninstallBadges(allApps, false);
        clearUninstallableItemList();
    }

    private void setVisibilityForAllUninstallBadges(AllAppsPagedView allApps, boolean visible) {
        ArrayList<IUninstallBadgeView> arrayList;
        if (allApps == null || (arrayList = this.mUninstallableItemArrayList) == null || arrayList.isEmpty()) {
            return;
        }
        for (IUninstallBadgeView iUninstallBadgeView : this.mUninstallableItemArrayList) {
            if (iUninstallBadgeView != null) {
                iUninstallBadgeView.setVisibilityForUninstallBadge(visible, 0);
            }
        }
    }

    private void setupAllUninstallableItemsList(AllAppsPagedView allApps) {
        clearUninstallableItemList();
        this.mUninstallableItemArrayList = new ArrayList<>();
        allApps.mapOverItems(true, new Workspace.ItemOperator() { // from class: com.lge.launcher3.uninstallmode.UninstallModeManager.12
            /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.view.View */
            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.android.launcher3.Workspace.ItemOperator
            public boolean evaluate(ItemInfo info, View v, View parent) {
                if (v != 0 && (v instanceof IUninstallBadgeView)) {
                    IUninstallBadgeView iUninstallBadgeView = (IUninstallBadgeView) v;
                    if (!iUninstallBadgeView.isUninstallableAllApps()) {
                        return false;
                    }
                    UninstallModeManager.this.mUninstallableItemArrayList.add(iUninstallBadgeView);
                }
                return false;
            }
        });
    }

    public boolean setUninstallTypeForBadgeViewAllApps(View item) {
        if (isDisabled()) {
            return false;
        }
        if (item == 0) {
            LGLog.i(TAG, String.format("addToUninstallableItemList() for AllApps : item is null.", item));
            return false;
        }
        if (!this.mIsBindingFinished) {
            return false;
        }
        Object tag = item.getTag();
        if (tag == null || !(tag instanceof ItemInfo)) {
            LGLog.i(TAG, String.format("addToUninstallableItemList() for AllApps : tag is null or not ItemInfo.", item));
            return false;
        }
        ItemInfo itemInfo = (ItemInfo) tag;
        IUninstallBadgeView iUninstallBadgeView = (IUninstallBadgeView) item;
        iUninstallBadgeView.setUninstallType(getUninstallType(itemInfo));
        if (!iUninstallBadgeView.isUninstallableAllApps()) {
            return false;
        }
        LGLog.i(TAG, String.format("addToUninstallableItemList() for AllApps : An itemType [%d]'s uninstall type is set.", Integer.valueOf(itemInfo.itemType)));
        if (isInUninstallMode() && !this.mUninstallableItemArrayList.contains(item)) {
            iUninstallBadgeView.setVisibilityForUninstallBadge(true, 0);
            if (!this.mUninstallableItemArrayList.contains(iUninstallBadgeView)) {
                this.mUninstallableItemArrayList.add(iUninstallBadgeView);
            }
        }
        return true;
    }

    public boolean isPowerSaveMode() {
        return this.mIsPowerSaveMode;
    }

    public void setUninstallTypeForItemsInFolder(final FolderPagedView folderPagedView) {
        ShortcutAndWidgetContainer shortcutsAndWidgets;
        if (folderPagedView != null) {
            for (int i = 0; i < folderPagedView.getChildCount(); i++) {
                CellLayout cellLayout = (CellLayout) folderPagedView.getChildAt(i);
                if (cellLayout != null && (shortcutsAndWidgets = cellLayout.getShortcutsAndWidgets()) != null) {
                    for (int i2 = 0; i2 < shortcutsAndWidgets.getChildCount(); i2++) {
                        View childAt = shortcutsAndWidgets.getChildAt(i2);
                        if (childAt != null) {
                            setUninstallTypeForBadgeView(childAt);
                        }
                    }
                }
            }
        }
    }
}
