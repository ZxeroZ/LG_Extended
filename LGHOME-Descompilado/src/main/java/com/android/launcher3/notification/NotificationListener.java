package com.android.launcher3.notification;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.util.Pair;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.util.PackageUserKey;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class NotificationListener extends NotificationListenerService {
    private static final int MSG_CANCEL_NOTIFICATION = 4;
    private static final int MSG_NOTIFICATION_FULL_REFRESH = 3;
    private static final int MSG_NOTIFICATION_POSTED = 1;
    private static final int MSG_NOTIFICATION_REMOVED = 2;
    private static final int MSG_RANKING_UPDATE = 5;
    private static final String TAG = "NotificationListener";
    private static boolean sIsConnected;
    private static NotificationListener sNotificationListenerInstance;
    private static ArrayList<NotificationsChangedListener> sNotificationsChangedListener = new ArrayList<>();
    private String mLastKeyDismissedByLauncher;
    private NotificationListenerService.Ranking mTempRanking = new NotificationListenerService.Ranking();
    private final Map<String, NotificationGroup> mNotificationGroupMap = new HashMap();
    private final Map<String, String> mNotificationGroupKeyMap = new HashMap();
    private Handler.Callback mWorkerCallback = new Handler.Callback() { // from class: com.android.launcher3.notification.NotificationListener.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            Object arrayList;
            int i = message.what;
            if (i == 1) {
                StatusBarNotification statusBarNotification = (StatusBarNotification) message.obj;
                if (NotificationListener.this.notificationIsValidForUI(statusBarNotification)) {
                    NotificationListener.this.mUiHandler.obtainMessage(1, NotificationListener.this.new NotificationPostedMsg(statusBarNotification)).sendToTarget();
                } else {
                    NotificationListener.this.mUiHandler.obtainMessage(2, NotificationListener.toKeyPair(statusBarNotification)).sendToTarget();
                }
                return true;
            }
            if (i == 2) {
                StatusBarNotification statusBarNotification2 = (StatusBarNotification) message.obj;
                NotificationListener.this.mUiHandler.obtainMessage(2, NotificationListener.toKeyPair(statusBarNotification2)).sendToTarget();
                NotificationGroup notificationGroup = (NotificationGroup) NotificationListener.this.mNotificationGroupMap.get(statusBarNotification2.getGroupKey());
                String key = statusBarNotification2.getKey();
                if (notificationGroup != null) {
                    notificationGroup.removeChildKey(key);
                    if (notificationGroup.isEmpty()) {
                        if (key.equals(NotificationListener.this.mLastKeyDismissedByLauncher)) {
                            NotificationListener.this.cancelNotification(notificationGroup.getGroupSummaryKey());
                        }
                        NotificationListener.this.mNotificationGroupMap.remove(statusBarNotification2.getGroupKey());
                    }
                }
                if (key.equals(NotificationListener.this.mLastKeyDismissedByLauncher)) {
                    NotificationListener.this.mLastKeyDismissedByLauncher = null;
                }
                return true;
            }
            if (i == 3) {
                if (NotificationListener.sIsConnected) {
                    NotificationListener notificationListener = NotificationListener.this;
                    arrayList = notificationListener.filterNotifications(notificationListener.getActiveNotifications());
                } else {
                    arrayList = new ArrayList();
                }
                NotificationListener.this.mUiHandler.obtainMessage(message.what, arrayList).sendToTarget();
            } else {
                if (i == 4) {
                    NotificationListener.this.mLastKeyDismissedByLauncher = (String) message.obj;
                    NotificationListener notificationListener2 = NotificationListener.this;
                    notificationListener2.cancelNotification(notificationListener2.mLastKeyDismissedByLauncher);
                    return true;
                }
                if (i == 5) {
                    for (StatusBarNotification statusBarNotification3 : NotificationListener.this.getActiveNotifications(((NotificationListenerService.RankingMap) message.obj).getOrderedKeys())) {
                        NotificationListener.this.updateGroupKeyIfNecessary(statusBarNotification3);
                    }
                    return true;
                }
            }
            return true;
        }
    };
    private Handler.Callback mUiCallback = new Handler.Callback() { // from class: com.android.launcher3.notification.NotificationListener.2
        /* JADX DEBUG: Multi-variable search result rejected for r3v1, resolved type: F */
        /* JADX DEBUG: Multi-variable search result rejected for r4v1, resolved type: S */
        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            int i = message.what;
            if (i != 1) {
                if (i == 2) {
                    if (NotificationListener.sNotificationsChangedListener != null) {
                        Pair pair = (Pair) message.obj;
                        Iterator it = NotificationListener.sNotificationsChangedListener.iterator();
                        while (it.hasNext()) {
                            ((NotificationsChangedListener) it.next()).onNotificationRemoved((PackageUserKey) pair.first, (NotificationKeyData) pair.second);
                        }
                    }
                } else if (i == 3 && NotificationListener.sNotificationsChangedListener != null) {
                    Iterator it2 = NotificationListener.sNotificationsChangedListener.iterator();
                    while (it2.hasNext()) {
                        ((NotificationsChangedListener) it2.next()).onNotificationFullRefresh((List) message.obj);
                    }
                }
            } else if (NotificationListener.sNotificationsChangedListener != null) {
                NotificationPostedMsg notificationPostedMsg = (NotificationPostedMsg) message.obj;
                Iterator it3 = NotificationListener.sNotificationsChangedListener.iterator();
                while (it3.hasNext()) {
                    ((NotificationsChangedListener) it3.next()).onNotificationPosted(notificationPostedMsg.packageUserKey, notificationPostedMsg.notificationKey, notificationPostedMsg.shouldBeFilteredOut);
                }
            }
            return true;
        }
    };
    private final Handler mWorkerHandler = new Handler(LauncherModel.getWorkerLooper(), this.mWorkerCallback);
    private final Handler mUiHandler = new Handler(Looper.getMainLooper(), this.mUiCallback);

    public interface NotificationsChangedListener {
        void onNotificationFullRefresh(List<StatusBarNotification> activeNotifications);

        void onNotificationPosted(PackageUserKey postedPackageUserKey, NotificationKeyData notificationKey, boolean shouldBeFilteredOut);

        void onNotificationRemoved(PackageUserKey removedPackageUserKey, NotificationKeyData notificationKey);
    }

    private NotificationListener() {
    }

    public static NotificationListener getInstanceIfConnected() {
        if (sIsConnected) {
            return sNotificationListenerInstance;
        }
        return null;
    }

    public static NotificationListener getInstance() {
        if (sNotificationListenerInstance == null) {
            sNotificationListenerInstance = new NotificationListener();
        }
        return sNotificationListenerInstance;
    }

    public void registerSystemService(Context context) {
        try {
            LGLog.d(TAG, "registerAsSystemService");
            registerAsSystemService(context, new ComponentName(context.getPackageName(), context.getClass().getCanonicalName()), -1);
        } catch (RemoteException e) {
            LGLog.d(TAG, "fail registerAsSystemService - " + e);
        }
    }

    public void unregisterSystemService() {
        try {
            LGLog.d(TAG, "unregisterSystemService");
            unregisterAsSystemService();
        } catch (RemoteException e) {
            LGLog.e(TAG, "Unable to unregisterSystemService", e);
        }
    }

    public static void setNotificationsChangedListener(NotificationsChangedListener listener) {
        if (!sNotificationsChangedListener.contains(listener)) {
            sNotificationsChangedListener.add(listener);
        }
        LGLog.d(TAG, "In setNotificationsChangedListener : " + String.valueOf(sNotificationsChangedListener.size()));
        NotificationListener notificationListener = sNotificationListenerInstance;
        if (notificationListener != null) {
            notificationListener.onNotificationFullRefresh();
        }
    }

    public static void refreshNotificationsChangedListener() {
        LGLog.d(TAG, "refreshNotificationsChangedListener");
        NotificationListener notificationListener = sNotificationListenerInstance;
        if (notificationListener != null) {
            notificationListener.onNotificationFullRefresh();
        }
    }

    public static void removeNotificationsChangedListener() {
        if (sNotificationsChangedListener.size() > 0) {
            sNotificationsChangedListener.clear();
        }
    }

    @Override // android.service.notification.NotificationListenerService
    public void onListenerConnected() {
        super.onListenerConnected();
        sIsConnected = true;
        onNotificationFullRefresh();
    }

    private void onNotificationFullRefresh() {
        this.mWorkerHandler.obtainMessage(3).sendToTarget();
    }

    @Override // android.service.notification.NotificationListenerService
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        sIsConnected = false;
    }

    @Override // android.service.notification.NotificationListenerService
    public void onNotificationPosted(final StatusBarNotification sbn) {
        LGLog.d(TAG, "onNotificationPosted : " + sbn);
        super.onNotificationPosted(sbn);
        if (sbn != null) {
            this.mWorkerHandler.obtainMessage(1, sbn).sendToTarget();
        }
    }

    private class NotificationPostedMsg {
        NotificationKeyData notificationKey;
        PackageUserKey packageUserKey;
        boolean shouldBeFilteredOut;

        NotificationPostedMsg(StatusBarNotification sbn) {
            this.packageUserKey = PackageUserKey.fromNotification(sbn);
            this.notificationKey = NotificationKeyData.fromNotification(sbn);
            this.shouldBeFilteredOut = NotificationListener.this.shouldBeFilteredOut(sbn);
        }
    }

    @Override // android.service.notification.NotificationListenerService
    public void onNotificationRemoved(final StatusBarNotification sbn) {
        LGLog.d(TAG, "onNotificationRemoved : " + sbn);
        super.onNotificationRemoved(sbn);
        if (sbn != null) {
            this.mWorkerHandler.obtainMessage(2, sbn).sendToTarget();
        }
    }

    @Override // android.service.notification.NotificationListenerService
    public void onNotificationRankingUpdate(NotificationListenerService.RankingMap rankingMap) {
        LGLog.d(TAG, "onNotificationRankingUpdate : " + rankingMap);
        this.mWorkerHandler.obtainMessage(5, rankingMap).sendToTarget();
    }

    public void cancelNotificationFromLauncher(String key) {
        LGLog.d(TAG, "cancelNotificationFromLauncher : " + key);
        this.mWorkerHandler.obtainMessage(4, key).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateGroupKeyIfNecessary(StatusBarNotification sbn) {
        String key = sbn.getKey();
        String str = this.mNotificationGroupKeyMap.get(key);
        String groupKey = sbn.getGroupKey();
        if (str == null || !str.equals(groupKey)) {
            this.mNotificationGroupKeyMap.put(key, groupKey);
            if (str != null && this.mNotificationGroupMap.containsKey(str)) {
                NotificationGroup notificationGroup = this.mNotificationGroupMap.get(str);
                notificationGroup.removeChildKey(key);
                if (notificationGroup.isEmpty()) {
                    this.mNotificationGroupMap.remove(str);
                }
            }
        }
        if (!sbn.isGroup() || groupKey == null) {
            return;
        }
        NotificationGroup notificationGroup2 = this.mNotificationGroupMap.get(groupKey);
        if (notificationGroup2 == null) {
            notificationGroup2 = new NotificationGroup();
            this.mNotificationGroupMap.put(groupKey, notificationGroup2);
        }
        if ((sbn.getNotification().flags & 512) != 0) {
            notificationGroup2.setGroupSummaryKey(key);
        } else {
            notificationGroup2.addChildKey(key);
        }
    }

    public List<StatusBarNotification> getNotificationsForKeys(List<NotificationKeyData> keys) {
        StatusBarNotification[] activeNotifications = getActiveNotifications((String[]) NotificationKeyData.extractKeysOnly(keys).toArray(new String[keys.size()]));
        return activeNotifications == null ? Collections.EMPTY_LIST : Arrays.asList(activeNotifications);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<StatusBarNotification> filterNotifications(StatusBarNotification[] notifications) {
        if (notifications == null) {
            return null;
        }
        HashSet hashSet = new HashSet();
        for (int i = 0; i < notifications.length; i++) {
            if (shouldBeFilteredOut(notifications[i])) {
                hashSet.add(Integer.valueOf(i));
            }
        }
        ArrayList arrayList = new ArrayList(notifications.length - hashSet.size());
        for (int i2 = 0; i2 < notifications.length; i2++) {
            if (!hashSet.contains(Integer.valueOf(i2))) {
                arrayList.add(notifications[i2]);
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldBeFilteredOut(StatusBarNotification sbn) {
        if (Build.VERSION.SDK_INT < 26) {
            return true;
        }
        getCurrentRanking().getRanking(sbn.getKey(), this.mTempRanking);
        if (!this.mTempRanking.canShowBadge()) {
            return true;
        }
        Notification notification = sbn.getNotification();
        if (!this.mTempRanking.getChannel().getId().equals(NotificationChannelCompat.DEFAULT_CHANNEL_ID) || (notification.flags & 2) == 0) {
            return ((notification.flags & 512) != 0) || ((notification.flags & 2) != 0) || (TextUtils.isEmpty(notification.extras.getCharSequence(NotificationCompat.EXTRA_TITLE)) && TextUtils.isEmpty(notification.extras.getCharSequence(NotificationCompat.EXTRA_TEXT)));
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean notificationIsValidForUI(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        updateGroupKeyIfNecessary(sbn);
        getCurrentRanking().getRanking(sbn.getKey(), this.mTempRanking);
        if (!this.mTempRanking.canShowBadge()) {
            return false;
        }
        if (this.mTempRanking.getChannel().getId().equals(NotificationChannelCompat.DEFAULT_CHANNEL_ID) && (notification.flags & 2) != 0) {
            return false;
        }
        boolean z = TextUtils.isEmpty(notification.extras.getCharSequence(NotificationCompat.EXTRA_TITLE)) && TextUtils.isEmpty(notification.extras.getCharSequence(NotificationCompat.EXTRA_TEXT));
        boolean z2 = (notification.flags & 512) != 0;
        LGLog.i(TAG, "notificationIsValidForUI() isGroupHeader = " + z2 + ", missingTitleAndText = " + z);
        return (z2 || z) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Pair<PackageUserKey, NotificationKeyData> toKeyPair(StatusBarNotification sbn) {
        return Pair.create(PackageUserKey.fromNotification(sbn), NotificationKeyData.fromNotification(sbn));
    }
}
