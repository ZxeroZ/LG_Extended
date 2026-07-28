package com.android.launcher3.popup;

import android.content.ComponentName;
import android.os.AsyncTask;
import android.service.notification.StatusBarNotification;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.badge.BadgeInfo;
import com.android.launcher3.dot.DotInfo;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.notification.NotificationKeyData;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.ShortcutUtil;
import com.android.launcher3.widget.WidgetListRowEntry;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes.dex */
public class PopupDataProvider implements NotificationListener.NotificationsChangedListener {
    private static final boolean LOGD = false;
    private static final String TAG = "PopupDataProvider";
    public static Map<PackageUserKey, BadgeInfo> mPackageUserToBadgeInfos = new HashMap();
    private final Launcher mLauncher;
    private MultiHashMap<ComponentKey, String> mDeepShortcutMap = new MultiHashMap<>();
    private ArrayList<WidgetListRowEntry> mAllWidgets = new ArrayList<>();
    private PopupDataChangeListener mChangeListener = PopupDataChangeListener.INSTANCE;

    public interface PopupDataChangeListener {
        public static final PopupDataChangeListener INSTANCE = new PopupDataChangeListener() { // from class: com.android.launcher3.popup.PopupDataProvider.PopupDataChangeListener.1
        };

        default void onNotificationDotsUpdated(Predicate<PackageUserKey> updatedDots) {
        }

        default void onWidgetsBound() {
        }

        default void trimNotifications(Map<PackageUserKey, DotInfo> updatedDots) {
        }
    }

    public PopupDataProvider(Launcher launcher) {
        this.mLauncher = launcher;
    }

    @Override // com.android.launcher3.notification.NotificationListener.NotificationsChangedListener
    public void onNotificationPosted(PackageUserKey postedPackageUserKey, NotificationKeyData notificationKey, boolean shouldBeFilteredOut) {
        boolean zAddOrUpdateNotificationKey;
        synchronized (PopupDataProvider.class) {
            BadgeInfo badgeInfo = mPackageUserToBadgeInfos.get(postedPackageUserKey);
            if (badgeInfo != null) {
                if (shouldBeFilteredOut) {
                    zAddOrUpdateNotificationKey = badgeInfo.removeNotificationKey(notificationKey);
                } else {
                    zAddOrUpdateNotificationKey = badgeInfo.addOrUpdateNotificationKey(notificationKey);
                }
                if (badgeInfo.getNotificationKeys().size() == 0) {
                    mPackageUserToBadgeInfos.remove(postedPackageUserKey);
                }
            } else if (shouldBeFilteredOut) {
                zAddOrUpdateNotificationKey = false;
            } else {
                BadgeInfo badgeInfo2 = new BadgeInfo(postedPackageUserKey);
                badgeInfo2.addOrUpdateNotificationKey(notificationKey);
                mPackageUserToBadgeInfos.put(postedPackageUserKey, badgeInfo2);
                zAddOrUpdateNotificationKey = true;
            }
            if (zAddOrUpdateNotificationKey) {
                this.mLauncher.updateIconBadges(Utilities.singletonHashSet(postedPackageUserKey));
            }
            if (zAddOrUpdateNotificationKey) {
                AppNotifierManager.getInstance(this.mLauncher).updateNotificationBadge(postedPackageUserKey, true);
            }
        }
    }

    @Override // com.android.launcher3.notification.NotificationListener.NotificationsChangedListener
    public void onNotificationRemoved(PackageUserKey removedPackageUserKey, NotificationKeyData notificationKey) {
        synchronized (PopupDataProvider.class) {
            BadgeInfo badgeInfo = mPackageUserToBadgeInfos.get(removedPackageUserKey);
            if (badgeInfo != null && badgeInfo.removeNotificationKey(notificationKey)) {
                if (badgeInfo.getNotificationKeys().size() == 0) {
                    mPackageUserToBadgeInfos.remove(removedPackageUserKey);
                    AppNotifierManager.getInstance(this.mLauncher).updateNotificationBadge(removedPackageUserKey, false);
                } else {
                    AppNotifierManager.getInstance(this.mLauncher).updateNotificationBadge(removedPackageUserKey, true);
                }
                this.mLauncher.updateIconBadges(Utilities.singletonHashSet(removedPackageUserKey));
                PopupContainerWithArrow open = PopupContainerWithArrow.getOpen(this.mLauncher);
                if (open != null && (open.mOriginalIcon.getTag() instanceof ItemInfo)) {
                    ItemInfo itemInfo = (ItemInfo) open.mOriginalIcon.getTag();
                    if (itemInfo.getTargetComponent() != null && removedPackageUserKey.mPackageName.equals(itemInfo.getTargetComponent().getPackageName())) {
                        open.trimNotifications(mPackageUserToBadgeInfos);
                    }
                }
            }
        }
    }

    private class NotiFullRefreshTask extends AsyncTask<List<StatusBarNotification>, Integer, Integer> {
        HashMap<PackageUserKey, BadgeInfo> updatedBadges;

        private NotiFullRefreshTask() {
            this.updatedBadges = null;
        }

        /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Integer doInBackground(List<StatusBarNotification>... params) {
            synchronized (PopupDataProvider.class) {
                this.updatedBadges = PopupDataProvider.this.doNotificationFullRefresh(params);
            }
            return 1;
        }

        /* JADX DEBUG: Method merged with bridge method: onPostExecute(Ljava/lang/Object;)V */
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            synchronized (PopupDataProvider.class) {
                PopupDataProvider.this.postNotificationFullRefresh(this.updatedBadges);
            }
        }
    }

    @Override // com.android.launcher3.notification.NotificationListener.NotificationsChangedListener
    public void onNotificationFullRefresh(List<StatusBarNotification> activeNotifications) {
        new NotiFullRefreshTask().execute(activeNotifications);
    }

    public HashMap<PackageUserKey, BadgeInfo> doNotificationFullRefresh(List<StatusBarNotification>[] activeNotifications) {
        if (activeNotifications == null || activeNotifications[0] == null) {
            return null;
        }
        HashMap<PackageUserKey, BadgeInfo> map = new HashMap<>(mPackageUserToBadgeInfos);
        mPackageUserToBadgeInfos.clear();
        for (StatusBarNotification statusBarNotification : activeNotifications[0]) {
            PackageUserKey packageUserKeyFromNotification = PackageUserKey.fromNotification(statusBarNotification);
            BadgeInfo badgeInfo = mPackageUserToBadgeInfos.get(packageUserKeyFromNotification);
            if (badgeInfo == null) {
                badgeInfo = new BadgeInfo(packageUserKeyFromNotification);
                mPackageUserToBadgeInfos.put(packageUserKeyFromNotification, badgeInfo);
            }
            badgeInfo.addOrUpdateNotificationKey(NotificationKeyData.fromNotification(statusBarNotification));
        }
        for (PackageUserKey packageUserKey : mPackageUserToBadgeInfos.keySet()) {
            BadgeInfo badgeInfo2 = map.get(packageUserKey);
            BadgeInfo badgeInfo3 = mPackageUserToBadgeInfos.get(packageUserKey);
            if (badgeInfo2 == null) {
                map.put(packageUserKey, badgeInfo3);
            } else if (!badgeInfo2.shouldBeInvalidated(badgeInfo3)) {
                map.remove(packageUserKey);
            }
        }
        return map;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postNotificationFullRefresh(HashMap<PackageUserKey, BadgeInfo> updatedBadges) {
        if (updatedBadges == null) {
            return;
        }
        if (!updatedBadges.isEmpty()) {
            this.mLauncher.updateIconBadges(updatedBadges.keySet());
            for (PackageUserKey packageUserKey : updatedBadges.keySet()) {
                AppNotifierManager.getInstance(this.mLauncher).updateNotificationBadge(packageUserKey, updatedBadges.get(packageUserKey).getNotificationCount() > 0);
            }
        }
        PopupContainerWithArrow open = PopupContainerWithArrow.getOpen(this.mLauncher);
        if (open != null) {
            open.notificationFullRefresh(updatedBadges);
        }
    }

    public void setDeepShortcutMap(MultiHashMap<ComponentKey, String> deepShortcutMapCopy) {
        this.mDeepShortcutMap = deepShortcutMapCopy;
    }

    public int getShortcutCountForItem(ItemInfo info) {
        ComponentName targetComponent;
        Integer numValueOf;
        if (!ShortcutUtil.supportsDeepShortcuts(info) || (targetComponent = info.getTargetComponent()) == null || (numValueOf = Integer.valueOf(Integer.parseInt((String) ((ArrayList) this.mDeepShortcutMap.get(new ComponentKey(targetComponent, info.user))).get(0)))) == null) {
            return 0;
        }
        return numValueOf.intValue();
    }

    public List<String> getShortcutIdsForItem(ItemInfo info) {
        if (!DeepShortcutManager.supportsShortcuts(info)) {
            return Collections.EMPTY_LIST;
        }
        ComponentName targetComponent = info.getTargetComponent();
        if (targetComponent == null) {
            return Collections.EMPTY_LIST;
        }
        List<String> list = (List) this.mDeepShortcutMap.get(new ComponentKey(targetComponent, info.user));
        return list == null ? Collections.EMPTY_LIST : list;
    }

    public BadgeInfo getBadgeInfoForItem(ItemInfo info) {
        if (DeepShortcutManager.supportsShortcuts(info)) {
            return mPackageUserToBadgeInfos.get(PackageUserKey.fromItemInfo(info));
        }
        return null;
    }

    public void setAllWidgets(ArrayList<WidgetListRowEntry> allWidgets) {
        this.mAllWidgets = allWidgets;
        this.mChangeListener.onWidgetsBound();
    }

    public void setChangeListener(PopupDataChangeListener listener) {
        if (listener == null) {
            listener = PopupDataChangeListener.INSTANCE;
        }
        this.mChangeListener = listener;
    }

    public ArrayList<WidgetListRowEntry> getAllWidgets() {
        return this.mAllWidgets;
    }

    public List<WidgetItem> getWidgetsForPackageUser(PackageUserKey packageUserKey) {
        for (WidgetListRowEntry widgetListRowEntry : this.mAllWidgets) {
            if (widgetListRowEntry.pkgItem.packageName.equals(packageUserKey.mPackageName)) {
                ArrayList arrayList = new ArrayList(widgetListRowEntry.widgets);
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    if (!((WidgetItem) it.next()).user.equals(packageUserKey.mUser)) {
                        it.remove();
                    }
                }
                if (arrayList.isEmpty()) {
                    return null;
                }
                return arrayList;
            }
        }
        return null;
    }

    public List<NotificationKeyData> getNotificationKeysForItem(ItemInfo info) {
        BadgeInfo badgeInfoForItem = getBadgeInfoForItem(info);
        return badgeInfoForItem == null ? Collections.EMPTY_LIST : badgeInfoForItem.getNotificationKeys();
    }

    public List<StatusBarNotification> getStatusBarNotificationsForKeys(List<NotificationKeyData> notificationKeys) {
        NotificationListener instanceIfConnected = NotificationListener.getInstanceIfConnected();
        if (instanceIfConnected == null) {
            return Collections.EMPTY_LIST;
        }
        return instanceIfConnected.getNotificationsForKeys(notificationKeys);
    }

    public void cancelNotification(String notificationKey) {
        NotificationListener instanceIfConnected = NotificationListener.getInstanceIfConnected();
        if (instanceIfConnected == null) {
            return;
        }
        instanceIfConnected.cancelNotificationFromLauncher(notificationKey);
    }

    public static void onDestroy() {
        Map<PackageUserKey, BadgeInfo> map = mPackageUserToBadgeInfos;
        if (map != null) {
            map.clear();
        }
    }

    public void dump(String prefix, PrintWriter writer) {
        writer.println(prefix + "PopupDataProvider:");
    }
}
