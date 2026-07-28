package com.android.launcher3.popup;

import android.app.ActivityOptions;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetHostView;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.util.InstantAppResolver;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.Themes;
import com.android.launcher3.widget.WidgetsBottomSheet;
import com.lge.launcher3.R;
import com.lge.launcher3.droptarget.LGUninstallDropTarget;
import com.lge.launcher3.uninstallmode.DeleteItemDialog;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
public abstract class SystemShortcut<T extends BaseDraggingActivity> extends ItemInfo implements View.OnClickListener {
    private final int mAccessibilityActionId;
    private final int mIconResId;
    protected final ItemInfo mItemInfo;
    private final int mLabelResId;
    protected final View mOriginalView;
    protected final T mTarget;
    public static final Factory<Launcher> WIDGETS = new Factory() { // from class: com.android.launcher3.popup.-$$Lambda$SystemShortcut$etj47UAe88R1uDsBrTlz11Pkkrw
        @Override // com.android.launcher3.popup.SystemShortcut.Factory
        public final SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, ItemInfo itemInfo) {
            return SystemShortcut.lambda$static$0((Launcher) baseDraggingActivity, itemInfo);
        }
    };
    public static final Factory<BaseDraggingActivity> APP_INFO = new Factory() { // from class: com.android.launcher3.popup.-$$Lambda$TPyhd7FWKEZzIuMbUwaZz745PCA
        @Override // com.android.launcher3.popup.SystemShortcut.Factory
        public final SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, ItemInfo itemInfo) {
            return new SystemShortcut.AppInfo(baseDraggingActivity, itemInfo);
        }
    };
    public static final Factory<BaseDraggingActivity> APP_INFO_SWIVEL = new Factory() { // from class: com.android.launcher3.popup.-$$Lambda$SystemShortcut$AUWQtC7_JcQRN8bsAcAAnaU3sxk
        @Override // com.android.launcher3.popup.SystemShortcut.Factory
        public final SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, ItemInfo itemInfo) {
            return SystemShortcut.lambda$static$1(baseDraggingActivity, itemInfo);
        }
    };
    public static final Factory<BaseDraggingActivity> INSTALL = new Factory() { // from class: com.android.launcher3.popup.-$$Lambda$SystemShortcut$5KuanRJHL3GHX5KdhPwrGTF9xd4
        @Override // com.android.launcher3.popup.SystemShortcut.Factory
        public final SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, ItemInfo itemInfo) {
            return SystemShortcut.lambda$static$2(baseDraggingActivity, itemInfo);
        }
    };
    public static final Factory<Launcher> DELETE = new Factory() { // from class: com.android.launcher3.popup.-$$Lambda$SystemShortcut$FgS18SGR2VByAEdBQ9AWokQ5wn4
        @Override // com.android.launcher3.popup.SystemShortcut.Factory
        public final SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, ItemInfo itemInfo) {
            return SystemShortcut.lambda$static$3((Launcher) baseDraggingActivity, itemInfo);
        }
    };
    public static final Factory<Launcher> REMOVE = new Factory() { // from class: com.android.launcher3.popup.-$$Lambda$SystemShortcut$02JNVbj4PzYjpptLEtm0vQFJCeo
        @Override // com.android.launcher3.popup.SystemShortcut.Factory
        public final SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, ItemInfo itemInfo) {
            return SystemShortcut.lambda$static$4((Launcher) baseDraggingActivity, itemInfo);
        }
    };
    public static final Factory<Launcher> WIDGET_SETTING = new Factory() { // from class: com.android.launcher3.popup.-$$Lambda$SystemShortcut$wAOKp8IqdIdjRsNGUatty5yLYTI
        @Override // com.android.launcher3.popup.SystemShortcut.Factory
        public final SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, ItemInfo itemInfo) {
            return SystemShortcut.lambda$static$5((Launcher) baseDraggingActivity, itemInfo);
        }
    };

    public interface Factory<T extends BaseDraggingActivity> {
        SystemShortcut<T> getShortcut(T activity, ItemInfo itemInfo);
    }

    public boolean isLeftGroup() {
        return false;
    }

    public SystemShortcut(int iconResId, int labelResId, T target, ItemInfo itemInfo) {
        this.mIconResId = iconResId;
        this.mLabelResId = labelResId;
        this.mAccessibilityActionId = labelResId;
        this.mTarget = target;
        this.mItemInfo = itemInfo;
        this.mOriginalView = null;
    }

    public SystemShortcut(int iconResId, int labelResId, T target, ItemInfo itemInfo, View originalView) {
        this.mIconResId = iconResId;
        this.mLabelResId = labelResId;
        this.mAccessibilityActionId = labelResId;
        this.mTarget = target;
        this.mItemInfo = itemInfo;
        this.mOriginalView = originalView;
    }

    public SystemShortcut(SystemShortcut<T> other) {
        this.mIconResId = other.mIconResId;
        this.mLabelResId = other.mLabelResId;
        this.mAccessibilityActionId = other.mAccessibilityActionId;
        this.mTarget = other.mTarget;
        this.mItemInfo = other.mItemInfo;
        this.mOriginalView = other.mOriginalView;
    }

    public void setIconAndLabelFor(View iconView, TextView labelView) {
        iconView.setBackgroundResource(this.mIconResId);
        labelView.setText(this.mLabelResId);
    }

    public void setIconAndContentDescriptionFor(ImageView view) {
        view.setImageResource(this.mIconResId);
        view.setContentDescription(view.getContext().getText(this.mLabelResId));
    }

    public AccessibilityNodeInfo.AccessibilityAction createAccessibilityAction(Context context) {
        return new AccessibilityNodeInfo.AccessibilityAction(this.mAccessibilityActionId, context.getText(this.mLabelResId));
    }

    public boolean hasHandlerForAction(int action) {
        return this.mAccessibilityActionId == action;
    }

    public Drawable getIcon(Context context, int colorAttr) {
        Drawable drawableMutate = context.getResources().getDrawable(this.mIconResId, context.getTheme()).mutate();
        drawableMutate.setTint(Themes.getAttrColor(context, colorAttr));
        return drawableMutate;
    }

    public Drawable getIcon(Context context) {
        return context.getResources().getDrawable(this.mIconResId, context.getTheme()).mutate();
    }

    public String getLabel(Context context) {
        return context.getString(this.mLabelResId);
    }

    static /* synthetic */ SystemShortcut lambda$static$0(Launcher launcher, ItemInfo itemInfo) {
        if (itemInfo.getTargetComponent() == null || launcher.getWidgetsForPackageUser(new PackageUserKey(itemInfo.getTargetComponent().getPackageName(), itemInfo.user)) == null) {
            return null;
        }
        return new Widgets(launcher, itemInfo);
    }

    public static class Widgets extends SystemShortcut<Launcher> {
        public Widgets(Launcher target, ItemInfo itemInfo) {
            super(Utilities.isLGUI10_0() ? R.drawable.ic_widget_black : R.drawable.ic_homescreen_appshortcut_widget, R.string.widget_button_text, target, itemInfo);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            AbstractFloatingView.closeAllOpenViews(this.mTarget);
            ((WidgetsBottomSheet) ((Launcher) this.mTarget).getLayoutInflater().inflate(R.layout.widgets_bottom_sheet, (ViewGroup) ((Launcher) this.mTarget).getDragLayer(), false)).populateAndShow(this.mItemInfo);
            ((Launcher) this.mTarget).getUserEventDispatcher().logActionOnControl(0, 2, view);
            ((Launcher) this.mTarget).getStatsLogManager().logger().withItemInfo(this.mItemInfo).log(StatsLogManager.LauncherEvent.LAUNCHER_SYSTEM_SHORTCUT_WIDGETS_TAP);
        }
    }

    public static class AppInfo extends SystemShortcut {
        public AppInfo(BaseDraggingActivity target, ItemInfo itemInfo) {
            super(R.drawable.recentapp_ic_info_normal, R.string.info_target_label, target, itemInfo);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            View originalIcon;
            AbstractFloatingView openView = AbstractFloatingView.getOpenView(this.mTarget, 2);
            if (openView instanceof PopupContainerWithArrow) {
                PopupContainerWithArrow popupContainerWithArrow = (PopupContainerWithArrow) openView;
                originalIcon = popupContainerWithArrow.getOriginalIcon();
                popupContainerWithArrow.close(false);
            } else {
                originalIcon = null;
            }
            if (originalIcon == null) {
                dismissTaskMenuView(this.mTarget);
            } else {
                view = originalIcon;
            }
            Rect viewBounds = this.mTarget.getViewBounds(view);
            ActivityOptions activityOptionsMakeBasic = ActivityOptions.makeBasic();
            if (activityOptionsMakeBasic != null) {
                activityOptionsMakeBasic.setSplashScreenStyle(0);
            }
            new PackageManagerHelper(this.mTarget).startDetailsActivityForInfo(this.mItemInfo, viewBounds, activityOptionsMakeBasic.toBundle());
            this.mTarget.getUserEventDispatcher().logActionOnControl(0, 7, view);
            this.mTarget.getStatsLogManager().logger().withItemInfo(this.mItemInfo).log(StatsLogManager.LauncherEvent.LAUNCHER_SYSTEM_SHORTCUT_APP_INFO_TAP);
        }
    }

    static /* synthetic */ SystemShortcut lambda$static$1(BaseDraggingActivity baseDraggingActivity, ItemInfo itemInfo) {
        if (itemInfo.itemType == 6) {
            LGLog.d(AppInfoSwivel.TAG, "not support AppInfo. itemInfo is deep shortcut. itemInfo.itemType = " + itemInfo.itemType);
            return null;
        }
        return new AppInfoSwivel(baseDraggingActivity, itemInfo);
    }

    public static class AppInfoSwivel extends SystemShortcut {
        private static final String TAG = "AppInfoSwivel";

        public AppInfoSwivel(BaseDraggingActivity target, ItemInfo itemInfo) {
            super(LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() ? R.drawable.ic_homescreen_appshortcut_info : R.drawable.ic_info, R.string.info_target_label, target, itemInfo);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            View originalIcon;
            AbstractFloatingView openView = AbstractFloatingView.getOpenView(this.mTarget, 2);
            if (openView instanceof PopupContainerWithArrow) {
                PopupContainerWithArrow popupContainerWithArrow = (PopupContainerWithArrow) openView;
                originalIcon = popupContainerWithArrow.getOriginalIcon();
                if (originalIcon == null) {
                    originalIcon = popupContainerWithArrow.getOriginalWidget();
                }
                popupContainerWithArrow.close(false);
            } else {
                originalIcon = null;
            }
            if (originalIcon == null) {
                dismissTaskMenuView(this.mTarget);
            } else {
                view = originalIcon;
            }
            Rect viewBounds = this.mTarget.getViewBounds(view);
            ActivityOptions activityOptionsMakeBasic = ActivityOptions.makeBasic();
            if (activityOptionsMakeBasic != null) {
                activityOptionsMakeBasic.setSplashScreenStyle(0);
            }
            new PackageManagerHelper(this.mTarget).startDetailsActivityForInfo(this.mItemInfo, viewBounds, activityOptionsMakeBasic.toBundle());
            this.mTarget.getUserEventDispatcher().logActionOnControl(0, 7, view);
            this.mTarget.getStatsLogManager().logger().withItemInfo(this.mItemInfo).log(StatsLogManager.LauncherEvent.LAUNCHER_SYSTEM_SHORTCUT_APP_INFO_TAP);
        }
    }

    static /* synthetic */ SystemShortcut lambda$static$2(BaseDraggingActivity baseDraggingActivity, ItemInfo itemInfo) {
        boolean z = true;
        boolean z2 = (itemInfo instanceof WorkspaceItemInfo) && ((WorkspaceItemInfo) itemInfo).hasStatusFlag(16);
        boolean zIsInstantApp = itemInfo instanceof com.android.launcher3.model.data.AppInfo ? InstantAppResolver.newInstance(baseDraggingActivity).isInstantApp((com.android.launcher3.model.data.AppInfo) itemInfo) : false;
        if (!z2 && !zIsInstantApp) {
            z = false;
        }
        if (z) {
            return new Install(baseDraggingActivity, itemInfo);
        }
        return null;
    }

    public static class Install extends SystemShortcut {
        public Install(BaseDraggingActivity target, ItemInfo itemInfo) {
            super(R.drawable.ic_install_no_shadow, R.string.install_drop_target_label, target, itemInfo);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            this.mTarget.lambda$startActivitySafely$4$Launcher(view, new PackageManagerHelper(view.getContext()).getMarketIntent(this.mItemInfo.getTargetComponent().getPackageName()), this.mItemInfo);
            AbstractFloatingView.closeAllOpenViews(this.mTarget);
        }
    }

    protected static void dismissTaskMenuView(BaseDraggingActivity activity) {
        AbstractFloatingView.closeOpenViews(activity, true, 3983);
    }

    static /* synthetic */ SystemShortcut lambda$static$3(Launcher launcher, ItemInfo itemInfo) {
        if (launcher == null || itemInfo == null) {
            LGLog.d(Delete.TAG, "not support uninstall. itemInfo = " + itemInfo);
            return null;
        }
        if (itemInfo.itemType == 6) {
            LGLog.d(Delete.TAG, "not support uninstall. itemInfo is deep shortcut. itemInfo.itemType = " + itemInfo.itemType);
            return null;
        }
        if (!LGUninstallDropTarget.supportsDrop(launcher.getApplicationContext(), itemInfo, 2)) {
            LGLog.d(Delete.TAG, "not support uninstall. itemInfo = " + itemInfo);
            return null;
        }
        return new Delete(launcher, itemInfo);
    }

    public static class Delete extends SystemShortcut<Launcher> {
        private static final String TAG = "Delete";

        public Delete(Launcher target, ItemInfo itemInfo) {
            super(Utilities.isLGUI10_0() ? R.drawable.ic_delete : R.drawable.ic_homescreen_appshortcut_delete, R.string.delete_target_delete_label, target, itemInfo);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            View originalIcon;
            AbstractFloatingView openView = AbstractFloatingView.getOpenView(this.mTarget, 2);
            if (openView == null || !(openView instanceof PopupContainerWithArrow)) {
                originalIcon = null;
            } else {
                PopupContainerWithArrow popupContainerWithArrow = (PopupContainerWithArrow) openView;
                originalIcon = popupContainerWithArrow.getOriginalIcon();
                popupContainerWithArrow.close(false);
            }
            LGLog.d(TAG, "Delete::onClick() iconView = " + originalIcon);
            if (originalIcon == null) {
                dismissTaskMenuView(this.mTarget);
            } else {
                LGUninstallDropTarget.startUninstallActivity((Launcher) this.mTarget, this.mItemInfo);
            }
        }
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0002: CONSTRUCTOR (r1v0 com.android.launcher3.Launcher), (r2v0 com.android.launcher3.model.data.ItemInfo) A[MD:(com.android.launcher3.Launcher, com.android.launcher3.model.data.ItemInfo):void (m)] (LINE:374) call: com.android.launcher3.popup.SystemShortcut.Remove.<init>(com.android.launcher3.Launcher, com.android.launcher3.model.data.ItemInfo):void type: CONSTRUCTOR */
    static /* synthetic */ SystemShortcut lambda$static$4(Launcher launcher, ItemInfo itemInfo) {
        return new Remove(launcher, itemInfo);
    }

    public static class Remove extends SystemShortcut<Launcher> {
        private static final String TAG = "Remove";

        public Remove(Launcher target, ItemInfo itemInfo) {
            super(Utilities.isLGUI10_0() ? R.drawable.ic_dnd : R.drawable.ic_homescreen_appshortcut_remove, R.string.app_shortcut_remove_from_home, target, itemInfo);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            View originalIcon;
            AbstractFloatingView openView = AbstractFloatingView.getOpenView(this.mTarget, 2);
            if (openView == null || !(openView instanceof PopupContainerWithArrow)) {
                originalIcon = null;
            } else {
                PopupContainerWithArrow popupContainerWithArrow = (PopupContainerWithArrow) openView;
                originalIcon = popupContainerWithArrow.getOriginalIcon();
                if (originalIcon == null) {
                    originalIcon = popupContainerWithArrow.getOriginalWidget();
                }
                popupContainerWithArrow.close(false);
            }
            LGLog.d(TAG, "Remove::onClick() iconView = " + originalIcon);
            if (originalIcon == null) {
                dismissTaskMenuView(this.mTarget);
            } else {
                DeleteItemDialog.showDialogFragment((Launcher) this.mTarget, this.mItemInfo, originalIcon, null);
            }
        }
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0002: CONSTRUCTOR (r1v0 com.android.launcher3.Launcher), (r2v0 com.android.launcher3.model.data.ItemInfo) A[MD:(com.android.launcher3.Launcher, com.android.launcher3.model.data.ItemInfo):void (m)] (LINE:411) call: com.android.launcher3.popup.SystemShortcut.WidgetSetting.<init>(com.android.launcher3.Launcher, com.android.launcher3.model.data.ItemInfo):void type: CONSTRUCTOR */
    static /* synthetic */ SystemShortcut lambda$static$5(Launcher launcher, ItemInfo itemInfo) {
        return new WidgetSetting(launcher, itemInfo);
    }

    public static class WidgetSetting extends SystemShortcut<Launcher> {
        private static final String ACTION_OPEN_WIDGET_SETTING = "com.lge.launcher3.intent.action.open_widget_setting";
        private static final String TAG = "WidgetSetting";

        public WidgetSetting(Launcher target, ItemInfo itemInfo) {
            super(R.drawable.ic_setting_black, R.string.sp_widget_settings_NORMAL, target, itemInfo);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            AppWidgetProviderInfo appWidgetInfo;
            AbstractFloatingView openView = AbstractFloatingView.getOpenView(this.mTarget, 2);
            LauncherAppWidgetHostView launcherAppWidgetHostView = null;
            if (openView == null || !(openView instanceof PopupContainerWithArrow)) {
                appWidgetInfo = null;
            } else {
                PopupContainerWithArrow popupContainerWithArrow = (PopupContainerWithArrow) openView;
                View originalWidget = popupContainerWithArrow.getOriginalWidget();
                if (originalWidget == null || !(originalWidget instanceof LauncherAppWidgetHostView)) {
                    appWidgetInfo = null;
                } else {
                    LauncherAppWidgetHostView launcherAppWidgetHostView2 = (LauncherAppWidgetHostView) originalWidget;
                    appWidgetInfo = launcherAppWidgetHostView2.getAppWidgetInfo();
                    launcherAppWidgetHostView = launcherAppWidgetHostView2;
                }
                popupContainerWithArrow.close(false);
            }
            String str = TAG;
            LGLog.d(str, "WidgetSetting::onClick() widgetView = " + launcherAppWidgetHostView);
            LGLog.d(str, "WidgetSetting::onClick() widgetInfo = " + appWidgetInfo);
            if (launcherAppWidgetHostView == null || appWidgetInfo == null || appWidgetInfo.configure == null || appWidgetInfo.configure.getClassName() == null) {
                dismissTaskMenuView(this.mTarget);
                return;
            }
            Intent intent = new Intent();
            intent.setClassName(appWidgetInfo.configure.getPackageName(), appWidgetInfo.configure.getClassName());
            intent.setFlags(270532608);
            intent.putExtra("appWidgetId", launcherAppWidgetHostView.getAppWidgetId());
            LGLog.d(str, "WidgetSetting::onClick() widgetInfo.configure.getPackageName() = " + appWidgetInfo.configure.getPackageName());
            LGLog.d(str, "WidgetSetting::onClick() widgetInfo.configure.getClassName() = " + appWidgetInfo.configure.getClassName());
            LGLog.d(str, "WidgetSetting::onClick() widgetView.getAppWidgetId() = " + launcherAppWidgetHostView.getAppWidgetId());
            try {
                ((Launcher) this.mTarget).startActivity(intent);
            } catch (Exception e) {
                LGLog.w(TAG, "Failed to start widget setting activity. ", e, new int[0]);
            }
        }
    }
}
