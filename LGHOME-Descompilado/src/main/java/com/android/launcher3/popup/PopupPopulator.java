package com.android.launcher3.popup;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.view.View;
import android.widget.ImageView;
import com.android.launcher3.Launcher;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.notification.NotificationInfo;
import com.android.launcher3.notification.NotificationItemView;
import com.android.launcher3.notification.NotificationKeyData;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.popup.PopupPopulator;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.DeepShortcutView;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.PackageUserKey;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.Utilities;
import com.lge.launcher3.wing.SystemShortcutView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes.dex */
public class PopupPopulator {
    public static final int MAX_ITEMS = 5;
    private static final int MAX_SHORTCUTS_IF_NOTIFICATIONS = 3;
    private static final int MAX_SHORTCUTS_IF_NOTIFICATIONS_SWIVEL = 2;
    static final int NUM_DYNAMIC = 2;
    private static final Comparator<ShortcutInfoCompat> SHORTCUT_RANK_COMPARATOR = new Comparator<ShortcutInfoCompat>() { // from class: com.android.launcher3.popup.PopupPopulator.1
        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public int compare(ShortcutInfoCompat a, ShortcutInfoCompat b) {
            if (a.isDeclaredInManifest() && !b.isDeclaredInManifest()) {
                return -1;
            }
            if (a.isDeclaredInManifest() || !b.isDeclaredInManifest()) {
                return Integer.compare(a.getRank(), b.getRank());
            }
            return 1;
        }
    };
    private static final String TAG = "PopupPopulator";

    public enum Item {
        SHORTCUT(R.layout.deep_shortcut, true),
        NOTIFICATION(R.layout.notification, false),
        SYSTEM_SHORTCUT(R.layout.system_shortcut, true),
        SYSTEM_SHORTCUT_ICON(R.layout.system_shortcut_icon_only, true),
        SHORTCUT_SWIVEL(Utilities.isLGUI10_0() ? R.layout.deep_shortcut_ux10_0 : R.layout.deep_shortcut_swivel, true),
        NOTIFICATION_SWIVEL(Utilities.isLGUI10_0() ? R.layout.notification_ux10_0 : R.layout.notification_swivel, false),
        SYSTEM_SHORTCUT_ICON_SWIVEL(Utilities.isLGUI10_0() ? R.layout.system_shortcut_icon_only_ux10_0 : R.layout.system_shortcut_icon_only_swivel, true);

        public final boolean isShortcut;
        public final int layoutId;

        Item(int layoutId, boolean isShortcut) {
            this.layoutId = layoutId;
            this.isShortcut = isShortcut;
        }
    }

    public static Item[] getItemsToPopulate(List<String> shortcutIds, List<NotificationKeyData> notificationKeys, List<SystemShortcut> systemShortcuts) {
        int iMin;
        int size;
        int i = notificationKeys.size() > 0 ? 1 : 0;
        int size2 = shortcutIds.size();
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            if (i != 0 && size2 > 3) {
                size2 = 3;
            }
            iMin = Math.min(5, size2 + i);
            size = systemShortcuts.size();
        } else {
            if (i != 0 && size2 > 3) {
                size2 = 3;
            }
            iMin = Math.min(5, size2 + i);
            size = systemShortcuts.size();
        }
        int i2 = iMin + size;
        Item[] itemArr = new Item[i2];
        for (int i3 = 0; i3 < i2; i3++) {
            itemArr[i3] = (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || Utilities.isLGUI10_0()) ? Item.SHORTCUT_SWIVEL : Item.SHORTCUT;
        }
        if (i != 0) {
            itemArr[0] = (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || Utilities.isLGUI10_0()) ? Item.NOTIFICATION_SWIVEL : Item.NOTIFICATION;
        }
        boolean z = !shortcutIds.isEmpty();
        for (int i4 = 0; i4 < systemShortcuts.size(); i4++) {
            if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || Utilities.isLGUI10_0()) {
                itemArr[(i2 - 1) - i4] = Item.SYSTEM_SHORTCUT_ICON_SWIVEL;
            } else {
                itemArr[(i2 - 1) - i4] = z ? Item.SYSTEM_SHORTCUT_ICON : Item.SYSTEM_SHORTCUT;
            }
        }
        return itemArr;
    }

    public static Item[] reverseItems(Item[] items) {
        if (items == null) {
            return null;
        }
        int length = items.length;
        Item[] itemArr = new Item[length];
        for (int i = 0; i < length; i++) {
            itemArr[i] = items[(length - i) - 1];
        }
        return itemArr;
    }

    public static List<ShortcutInfoCompat> sortAndFilterShortcuts(List<ShortcutInfoCompat> shortcuts, String shortcutIdToRemoveFirst) {
        if (shortcutIdToRemoveFirst != null) {
            Iterator<ShortcutInfoCompat> it = shortcuts.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                if (it.next().getId().equals(shortcutIdToRemoveFirst)) {
                    it.remove();
                    break;
                }
            }
        }
        Collections.sort(shortcuts, SHORTCUT_RANK_COMPARATOR);
        if (shortcuts.size() <= 5) {
            return shortcuts;
        }
        ArrayList arrayList = new ArrayList(5);
        int size = shortcuts.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            ShortcutInfoCompat shortcutInfoCompat = shortcuts.get(i2);
            int size2 = arrayList.size();
            if (size2 < 5) {
                arrayList.add(shortcutInfoCompat);
                if (shortcutInfoCompat.isDynamic()) {
                    i++;
                }
            } else if (shortcutInfoCompat.isDynamic() && i < 2) {
                i++;
                arrayList.remove(size2 - i);
                arrayList.add(shortcutInfoCompat);
            }
        }
        return arrayList;
    }

    public static Runnable createUpdateRunnable(final Launcher launcher, final ItemInfo originalInfo, final Handler uiHandler, final PopupContainerWithArrow container, final List<String> shortcutIds, final List<DeepShortcutView> shortcutViews, final List<NotificationKeyData> notificationKeys, final NotificationItemView notificationView, final List<SystemShortcut> systemShortcuts, final List<View> systemShortcutViews, final AppWidgetProviderInfo originalWidgetInfo) {
        return new AnonymousClass2(notificationKeys, launcher, originalInfo, uiHandler, container, originalWidgetInfo, originalInfo.getTargetComponent(), shortcutIds, originalInfo.user, shortcutViews, systemShortcuts, systemShortcutViews);
    }

    /* JADX INFO: renamed from: com.android.launcher3.popup.PopupPopulator$2, reason: invalid class name */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ ComponentName val$activity;
        final /* synthetic */ PopupContainerWithArrow val$container;
        final /* synthetic */ Launcher val$launcher;
        final /* synthetic */ List val$notificationKeys;
        final /* synthetic */ ItemInfo val$originalInfo;
        final /* synthetic */ AppWidgetProviderInfo val$originalWidgetInfo;
        final /* synthetic */ List val$shortcutIds;
        final /* synthetic */ List val$shortcutViews;
        final /* synthetic */ List val$systemShortcutViews;
        final /* synthetic */ List val$systemShortcuts;
        final /* synthetic */ Handler val$uiHandler;
        final /* synthetic */ UserHandle val$user;

        AnonymousClass2(final List val$notificationKeys, final Launcher val$launcher, final ItemInfo val$originalInfo, final Handler val$uiHandler, final PopupContainerWithArrow val$container, final AppWidgetProviderInfo val$originalWidgetInfo, final ComponentName val$activity, final List val$shortcutIds, final UserHandle val$user, final List val$shortcutViews, final List val$systemShortcuts, final List val$systemShortcutViews) {
            this.val$notificationKeys = val$notificationKeys;
            this.val$launcher = val$launcher;
            this.val$originalInfo = val$originalInfo;
            this.val$uiHandler = val$uiHandler;
            this.val$container = val$container;
            this.val$originalWidgetInfo = val$originalWidgetInfo;
            this.val$activity = val$activity;
            this.val$shortcutIds = val$shortcutIds;
            this.val$user = val$user;
            this.val$shortcutViews = val$shortcutViews;
            this.val$systemShortcuts = val$systemShortcuts;
            this.val$systemShortcutViews = val$systemShortcutViews;
        }

        @Override // java.lang.Runnable
        public void run() {
            final List listEmptyList;
            if (!this.val$notificationKeys.isEmpty()) {
                NotificationListener instanceIfConnected = NotificationListener.getInstanceIfConnected();
                if (instanceIfConnected == null) {
                    listEmptyList = Collections.emptyList();
                } else {
                    Stream<StatusBarNotification> stream = instanceIfConnected.getNotificationsForKeys(this.val$notificationKeys).stream();
                    final Launcher launcher = this.val$launcher;
                    final ItemInfo itemInfo = this.val$originalInfo;
                    listEmptyList = (List) stream.map(new Function() { // from class: com.android.launcher3.popup.-$$Lambda$PopupPopulator$2$ylQCx4Xd15956_7aF-BRiFbZgaI
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            return PopupPopulator.AnonymousClass2.lambda$run$0(launcher, itemInfo, (StatusBarNotification) obj);
                        }
                    }).collect(Collectors.toList());
                }
                Handler handler = this.val$uiHandler;
                final PopupContainerWithArrow popupContainerWithArrow = this.val$container;
                handler.post(new Runnable() { // from class: com.android.launcher3.popup.-$$Lambda$PopupPopulator$2$r9loGBJnrXUojwS1h1OGzz2bj0Q
                    @Override // java.lang.Runnable
                    public final void run() {
                        popupContainerWithArrow.applyNotificationInfos(listEmptyList);
                    }
                });
            }
            if (this.val$originalWidgetInfo == null) {
                List<ShortcutInfoCompat> listSortAndFilterShortcuts = PopupPopulator.sortAndFilterShortcuts(DeepShortcutManager.getInstance(this.val$launcher).queryForShortcutsContainer(this.val$activity, this.val$shortcutIds, this.val$user), null);
                for (int i = 0; i < listSortAndFilterShortcuts.size() && i < this.val$shortcutViews.size(); i++) {
                    ShortcutInfoCompat shortcutInfoCompat = listSortAndFilterShortcuts.get(i);
                    ShortcutInfo shortcutInfo = new ShortcutInfo(shortcutInfoCompat, this.val$launcher);
                    shortcutInfo.setIcon(LauncherIcons.createShortcutIcon(shortcutInfoCompat, this.val$launcher, false));
                    shortcutInfo.rank = i;
                    this.val$uiHandler.post(new UpdateShortcutChild(this.val$container, (DeepShortcutView) this.val$shortcutViews.get(i), shortcutInfo, shortcutInfoCompat));
                }
            }
            for (int i2 = 0; i2 < this.val$systemShortcuts.size(); i2++) {
                this.val$uiHandler.post(new UpdateSystemShortcutChild(this.val$container, (View) this.val$systemShortcutViews.get(i2), (SystemShortcut) this.val$systemShortcuts.get(i2), this.val$launcher, this.val$originalInfo));
            }
            this.val$uiHandler.post(new Runnable() { // from class: com.android.launcher3.popup.PopupPopulator.2.1
                @Override // java.lang.Runnable
                public void run() {
                    if (AnonymousClass2.this.val$originalWidgetInfo != null) {
                        AnonymousClass2.this.val$launcher.refreshAndBindWidgetsForPackageUser(PackageUserKey.fromWidgetInfo(AnonymousClass2.this.val$originalWidgetInfo));
                    } else {
                        AnonymousClass2.this.val$launcher.refreshAndBindWidgetsForPackageUser(PackageUserKey.fromItemInfo(AnonymousClass2.this.val$originalInfo));
                    }
                }
            });
        }

        /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0002: CONSTRUCTOR 
          (r1v0 com.android.launcher3.Launcher)
          (r3v0 android.service.notification.StatusBarNotification)
          (r2v0 com.android.launcher3.model.data.ItemInfo)
         A[MD:(android.content.Context, android.service.notification.StatusBarNotification, com.android.launcher3.model.data.ItemInfo):void (m)] (LINE:236) call: com.android.launcher3.notification.NotificationInfo.<init>(android.content.Context, android.service.notification.StatusBarNotification, com.android.launcher3.model.data.ItemInfo):void type: CONSTRUCTOR */
        static /* synthetic */ NotificationInfo lambda$run$0(Launcher launcher, ItemInfo itemInfo, StatusBarNotification statusBarNotification) {
            return new NotificationInfo(launcher, statusBarNotification, itemInfo);
        }
    }

    private static class UpdateShortcutChild implements Runnable {
        private final PopupContainerWithArrow mContainer;
        private final ShortcutInfoCompat mDetail;
        private final DeepShortcutView mShortcutChild;
        private final ShortcutInfo mShortcutChildInfo;

        public UpdateShortcutChild(PopupContainerWithArrow container, DeepShortcutView shortcutChild, ShortcutInfo shortcutChildInfo, ShortcutInfoCompat detail) {
            this.mContainer = container;
            this.mShortcutChild = shortcutChild;
            this.mShortcutChildInfo = shortcutChildInfo;
            this.mDetail = detail;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mShortcutChild.applyShortcutInfo(this.mShortcutChildInfo, this.mDetail, this.mContainer);
        }
    }

    private static class UpdateNotificationChild implements Runnable {
        private List<NotificationInfo> mNotificationInfos;
        private NotificationItemView mNotificationView;

        public UpdateNotificationChild(NotificationItemView notificationView, List<NotificationInfo> notificationInfos) {
            this.mNotificationView = notificationView;
            this.mNotificationInfos = notificationInfos;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mNotificationView.applyNotificationInfos(this.mNotificationInfos);
        }
    }

    private static class UpdateSystemShortcutChild implements Runnable {
        private final PopupContainerWithArrow mContainer;
        private final ItemInfo mItemInfo;
        private final Launcher mLauncher;
        private final View mSystemShortcutChild;
        private final SystemShortcut mSystemShortcutInfo;

        public UpdateSystemShortcutChild(PopupContainerWithArrow container, View systemShortcutChild, SystemShortcut systemShortcut, Launcher launcher, ItemInfo originalInfo) {
            this.mContainer = container;
            this.mSystemShortcutChild = systemShortcutChild;
            this.mSystemShortcutInfo = systemShortcut;
            this.mLauncher = launcher;
            this.mItemInfo = originalInfo;
        }

        @Override // java.lang.Runnable
        public void run() {
            PopupPopulator.initializeSystemShortcut(this.mSystemShortcutChild.getContext(), this.mSystemShortcutChild, this.mSystemShortcutInfo);
            this.mSystemShortcutChild.setOnClickListener(this.mSystemShortcutInfo);
        }
    }

    public static void initializeSystemShortcut(Context context, View view, SystemShortcut info) {
        if (view instanceof DeepShortcutView) {
            DeepShortcutView deepShortcutView = (DeepShortcutView) view;
            deepShortcutView.getIconView().setBackground(info.getIcon(context, android.R.attr.textColorTertiary));
            deepShortcutView.getBubbleText().setText(info.getLabel(context));
            deepShortcutView.getBubbleText().setTextColor(R.color.deep_shortcut_text_color);
        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            imageView.setImageDrawable(info.getIcon(context, android.R.attr.textColorHint));
            imageView.setContentDescription(info.getLabel(context));
        } else if (view instanceof SystemShortcutView) {
            SystemShortcutView systemShortcutView = (SystemShortcutView) view;
            ImageView imageView2 = systemShortcutView.mIconView;
            imageView2.setImageDrawable(info.getIcon(context));
            imageView2.setContentDescription(info.getLabel(context));
            systemShortcutView.mTextView.setText(info.getLabel(context));
        }
        view.setTag(info);
    }
}
