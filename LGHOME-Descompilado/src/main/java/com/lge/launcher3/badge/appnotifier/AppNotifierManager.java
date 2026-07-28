package com.lge.launcher3.badge.appnotifier;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import com.android.launcher3.badge.BadgeInfo;
import com.android.launcher3.popup.PopupDataProvider;
import com.android.launcher3.util.PackageUserKey;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class AppNotifierManager {
    private static final String ACTION_TAG = "action";
    private static final int APP_NOTIFIER_XML = 2132017160;
    private static final String AU_SMS_CLASS_NAME = "com.kddi.android.cmail.util.BadgeCountNotifier";
    private static final String AU_SMS_REPLACE_CLASS_NAME = "com.kddi.android.cmail.ui.list.ThreadListActivity";
    private static final String COMPONENT_TAG = "component";
    private static final boolean DEBUG = false;
    private static final String EXTRA_MAIN_TAG = "extra";
    private static final String NAME_TAG = "name";
    private static final String NOTIFICATION_BADGE_ICON_TYPE = "notification_badge_icon_type";
    private static final String TAG = "AppNotifier.Manager";
    private static final String USAGE_TAG = "usage";
    private static final String XMLNS = "com.lge.launcher/xmlns";
    private static AppNotifierManager sInstance;
    private ContentObserver mBadgeNumberObserver;
    private HashMap<String, Set<String>> mCacheComponentsMap;
    private Context mContext;
    private ContentObserver mIconBadgeObserver;
    private ContentObserver mSecretModeObserver;
    private HashMap<String, ActionSpec> mActionSpecs = new HashMap<>();
    private HashSet<String> mComponents = new HashSet<>();
    private ConcurrentHashMap<IAppNotifierView, AppNotifierData> mRegisteredView = new ConcurrentHashMap<>();
    private ConcurrentHashMap<IAppNotifierGroup, ArrayList<AppNotifierData>> mRegisteredGroup = new ConcurrentHashMap<>();
    private HashSet<AppNotifierData> mNotiComponents = new HashSet<>();
    private AppNotifierDrawer mDrawer = new AppNotifierDrawer();
    private Boolean mIsSecretModeOn = false;
    private Map<PackageUserKey, BadgeInfo> mPackageUserToBadgeInfos = PopupDataProvider.mPackageUserToBadgeInfos;
    private int mEnableIconBadge = 1;
    private int isShowNumber = 0;

    private AppNotifierManager(Context context) {
        this.mContext = context.getApplicationContext();
        initAppNotifierList();
        initSecretMode();
        initIconBadgeObserving();
        this.mCacheComponentsMap = new HashMap<>();
    }

    public static AppNotifierManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AppNotifierManager(context);
        }
        return sInstance;
    }

    public static void destoryInstance() {
        AppNotifierManager appNotifierManager = sInstance;
        if (appNotifierManager != null) {
            appNotifierManager.mRegisteredGroup.clear();
            sInstance.mRegisteredView.clear();
            sInstance = null;
        }
    }

    private void initAppNotifierList() {
        parseXml();
        this.mComponents.addAll(AppNotifierRecorder.getAllName(this.mContext));
    }

    private void parseXml() {
        XmlResourceParser xml = this.mContext.getResources().getXml(R.xml.appnotifier);
        HashMap map = new HashMap();
        HashSet hashSet = new HashSet();
        while (true) {
            String attributeValue = null;
            while (true) {
                try {
                    int next = xml.next();
                    if (next == 1) {
                        return;
                    }
                    if (next == 2) {
                        String name = xml.getName();
                        if ("action".equals(name)) {
                            attributeValue = xml.getAttributeValue(XMLNS, NAME_TAG);
                        } else if (EXTRA_MAIN_TAG.equals(name)) {
                            String attributeValue2 = xml.getAttributeValue(XMLNS, NAME_TAG);
                            map.put(attributeValue2, new ExtraSpec(attributeValue2, xml.getAttributeValue(XMLNS, USAGE_TAG)));
                        } else if ("component".equals(name)) {
                            hashSet.add(xml.getAttributeValue(XMLNS, NAME_TAG));
                        }
                    } else if (next == 3 && "action".equals(xml.getName())) {
                        break;
                    }
                } catch (IOException | XmlPullParserException e) {
                    e.printStackTrace();
                    return;
                }
            }
            ActionSpec actionSpec = new ActionSpec(attributeValue, map, hashSet);
            this.mActionSpecs.put(actionSpec.getAction(), actionSpec);
            this.mComponents.addAll(hashSet);
            map.clear();
            hashSet.clear();
        }
    }

    public void destroyAppNotifier() {
        if (this.mSecretModeObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mSecretModeObserver);
        }
        if (this.mIconBadgeObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mIconBadgeObserver);
        }
        if (this.mBadgeNumberObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mBadgeNumberObserver);
        }
    }

    public AppNotifierDrawer registerAppNotifier(IAppNotifierView view, AppNotifierData appData) {
        BadgeInfo badgeInfo;
        AppNotifierData appNotifierDataPutIfAbsent = this.mRegisteredView.putIfAbsent(view, appData);
        if (appNotifierDataPutIfAbsent != null && !appNotifierDataPutIfAbsent.equals(appData)) {
            this.mRegisteredView.replace(view, appData);
        }
        int notificationCount = 0;
        if (this.mNotiComponents.contains(appData) && (badgeInfo = this.mPackageUserToBadgeInfos.get(new PackageUserKey(appData.packageName, appData.user))) != null) {
            notificationCount = badgeInfo.getNotificationCount();
        }
        notifyCount(appData, notificationCount);
        updateSecretModeIconforNumberBadge(this.mContext, "jp.naver.line.android");
        return this.mDrawer;
    }

    public void unregisterAppNotifier(IAppNotifierView view) {
        if (view == null) {
            return;
        }
        this.mRegisteredView.remove(view);
    }

    public void notifyAllCount() {
        for (Map.Entry<IAppNotifierView, AppNotifierData> entry : this.mRegisteredView.entrySet()) {
            int notificationCount = 0;
            if (this.mNotiComponents.contains(entry.getValue())) {
                BadgeInfo badgeInfo = this.mPackageUserToBadgeInfos.get(new PackageUserKey(entry.getValue().packageName, entry.getValue().user));
                if (badgeInfo != null) {
                    notificationCount = badgeInfo.getNotificationCount();
                }
            }
            notifyCount(entry.getValue(), notificationCount);
        }
        updateSecretModeIconforNumberBadge(this.mContext, "jp.naver.line.android");
    }

    private void notifyCount(final AppNotifierData appData, final int count) {
        if (this.mRegisteredView.containsValue(appData)) {
            for (Map.Entry<IAppNotifierView, AppNotifierData> entry : this.mRegisteredView.entrySet()) {
                if (entry.getValue().equals(appData)) {
                    if (this.mEnableIconBadge == 1) {
                        entry.getKey().onUpdateAppNotifier(count);
                    } else {
                        entry.getKey().onUpdateAppNotifier(0);
                    }
                }
            }
        }
    }

    private void notifyGroupCount(ArrayList<AppNotifierData> components, int count) {
        for (Map.Entry<IAppNotifierGroup, ArrayList<AppNotifierData>> entry : this.mRegisteredGroup.entrySet()) {
            if (entry.getValue().equals(components)) {
                if (this.mEnableIconBadge == 1) {
                    entry.getKey().onUpdateAppNotifier(count);
                } else {
                    entry.getKey().onUpdateAppNotifier(0);
                }
                if (LGHomeFeature.isEnableDefaultHome()) {
                    return;
                }
            }
        }
    }

    private void notifyGroupCount(AppNotifierData componentName, int count) {
        Iterator<Map.Entry<IAppNotifierGroup, ArrayList<AppNotifierData>>> it = this.mRegisteredGroup.entrySet().iterator();
        while (it.hasNext()) {
            ArrayList<AppNotifierData> value = it.next().getValue();
            if (value.contains(componentName)) {
                int notificationCount = 0;
                for (AppNotifierData appNotifierData : value) {
                    if (this.mNotiComponents.contains(appNotifierData)) {
                        BadgeInfo badgeInfo = this.mPackageUserToBadgeInfos.get(new PackageUserKey(appNotifierData.packageName, appNotifierData.user));
                        if (badgeInfo != null) {
                            notificationCount += badgeInfo.getNotificationCount();
                        }
                    }
                }
                notifyGroupCount(value, notificationCount);
            }
        }
    }

    public void notifyAllGroupCount() {
        Iterator<Map.Entry<IAppNotifierGroup, ArrayList<AppNotifierData>>> it = this.mRegisteredGroup.entrySet().iterator();
        while (it.hasNext()) {
            ArrayList<AppNotifierData> value = it.next().getValue();
            int notificationCount = 0;
            if (this.mEnableIconBadge == 1) {
                for (AppNotifierData appNotifierData : value) {
                    if (this.mNotiComponents.contains(appNotifierData)) {
                        BadgeInfo badgeInfo = this.mPackageUserToBadgeInfos.get(new PackageUserKey(appNotifierData.packageName, appNotifierData.user));
                        if (badgeInfo != null) {
                            notificationCount += badgeInfo.getNotificationCount();
                        }
                    }
                }
                notifyGroupCount(value, notificationCount);
            } else {
                notifyGroupCount(value, 0);
            }
        }
    }

    public String getCountDescription(int count) {
        if (count == 1) {
            return this.mContext.getString(R.string.talkback_appnotifier_one_newevent);
        }
        if (count >= AppNotifierConstant.DEFAULT_MAX_NUMBER) {
            return String.format(this.mContext.getString(R.string.talkback_appnotifier_newevents_more), Integer.valueOf(AppNotifierConstant.DEFAULT_MAX_NUMBER));
        }
        if (count <= 1 || count >= AppNotifierConstant.DEFAULT_MAX_NUMBER) {
            return null;
        }
        return String.format(this.mContext.getString(R.string.talkback_appnotifier_newevents), Integer.valueOf(count));
    }

    public AppNotifierDrawer registerAppNotifierGroup(IAppNotifierGroup view, ArrayList<AppNotifierData> componentNames) {
        if (this.mRegisteredGroup.containsKey(view)) {
            this.mRegisteredGroup.replace(view, componentNames);
        } else {
            this.mRegisteredGroup.put(view, componentNames);
        }
        int notificationCount = 0;
        for (AppNotifierData appNotifierData : componentNames) {
            if (this.mNotiComponents.contains(appNotifierData)) {
                BadgeInfo badgeInfo = this.mPackageUserToBadgeInfos.get(new PackageUserKey(appNotifierData.packageName, appNotifierData.user));
                if (badgeInfo != null) {
                    notificationCount += badgeInfo.getNotificationCount();
                }
            }
        }
        notifyGroupCount(componentNames, notificationCount);
        return this.mDrawer;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r4v0, resolved type: com.lge.launcher3.badge.appnotifier.IAppNotifierGroup */
    /* JADX WARN: Multi-variable type inference failed */
    public void unregisterAppNotifierGroup(IAppNotifierGroup view) {
        if (view == 0) {
            return;
        }
        LGLog.i(TAG, "Unregister group: " + ((View) view).getTag());
        this.mRegisteredGroup.remove(view);
    }

    private void initSecretMode() {
        if (this.mContext.getResources().getBoolean(R.bool.config_feature_support_secretmode)) {
            this.mSecretModeObserver = new ContentObserver(new Handler()) { // from class: com.lge.launcher3.badge.appnotifier.AppNotifierManager.1
                @Override // android.database.ContentObserver
                public void onChange(boolean selfChange) {
                    AppNotifierManager appNotifierManager = AppNotifierManager.this;
                    appNotifierManager.mIsSecretModeOn = Boolean.valueOf(AppNotifierUtils.isSecretModeOn(appNotifierManager.mContext));
                    AppNotifierManager appNotifierManager2 = AppNotifierManager.this;
                    appNotifierManager2.updateSecretModeIconforNumberBadge(appNotifierManager2.mContext, "jp.naver.line.android");
                }
            };
            this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("secret_mode"), true, this.mSecretModeObserver);
            this.mIsSecretModeOn = Boolean.valueOf(AppNotifierUtils.isSecretModeOn(this.mContext));
        }
    }

    private void initIconBadgeObserving() {
        this.mEnableIconBadge = Settings.Secure.getInt(this.mContext.getContentResolver(), "notification_badging", 1);
        this.isShowNumber = Settings.Secure.getInt(this.mContext.getContentResolver(), NOTIFICATION_BADGE_ICON_TYPE, 0);
        this.mIconBadgeObserver = new ContentObserver(new Handler()) { // from class: com.lge.launcher3.badge.appnotifier.AppNotifierManager.2
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                AppNotifierManager appNotifierManager = AppNotifierManager.this;
                appNotifierManager.mEnableIconBadge = Settings.Secure.getInt(appNotifierManager.mContext.getContentResolver(), "notification_badging", 1);
                LGLog.i(AppNotifierManager.TAG, "initIconBadgeObserving : mEnableIconBadge update to " + AppNotifierManager.this.mEnableIconBadge);
                if (AppNotifierManager.this.mEnableIconBadge == 1) {
                    AppNotifierManager appNotifierManager2 = AppNotifierManager.this;
                    appNotifierManager2.isShowNumber = Settings.Secure.getInt(appNotifierManager2.mContext.getContentResolver(), AppNotifierManager.NOTIFICATION_BADGE_ICON_TYPE, 0);
                    LGLog.i(AppNotifierManager.TAG, "initIconBadgeObserving : isShowNumber update to " + AppNotifierManager.this.isShowNumber);
                }
                AppNotifierManager.this.updateNotificationBadgeStatus();
            }
        };
        this.mBadgeNumberObserver = new ContentObserver(new Handler()) { // from class: com.lge.launcher3.badge.appnotifier.AppNotifierManager.3
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                AppNotifierManager appNotifierManager = AppNotifierManager.this;
                appNotifierManager.isShowNumber = Settings.Secure.getInt(appNotifierManager.mContext.getContentResolver(), AppNotifierManager.NOTIFICATION_BADGE_ICON_TYPE, 0);
                LGLog.i(AppNotifierManager.TAG, "initIconBadgeObserving : isShowNumber update to " + AppNotifierManager.this.isShowNumber);
                AppNotifierManager.this.updateNotificationBadgeStatus();
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("notification_badging"), true, this.mIconBadgeObserver);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(NOTIFICATION_BADGE_ICON_TYPE), true, this.mBadgeNumberObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSecretModeIconforNumberBadge(Context context, String packageName) {
        BadgeInfo badgeInfo;
        ArrayList<AppNotifierData> arrayList = new ArrayList();
        for (Map.Entry<IAppNotifierView, AppNotifierData> entry : this.mRegisteredView.entrySet()) {
            if (entry.getValue().packageName.equals(packageName)) {
                arrayList.add(entry.getValue());
            }
        }
        for (AppNotifierData appNotifierData : arrayList) {
            int notificationCount = 0;
            if (!this.mIsSecretModeOn.booleanValue() && (badgeInfo = this.mPackageUserToBadgeInfos.get(new PackageUserKey(appNotifierData.packageName, appNotifierData.user))) != null) {
                notificationCount = badgeInfo.getNotificationCount();
            }
            notifyCount(appNotifierData, notificationCount);
        }
    }

    private Set<String> getComponetsFromPackageName(String packageName) {
        boolean z;
        HashSet hashSet = new HashSet();
        String[] stringArray = this.mContext.getResources().getStringArray(R.array.exclude_noti_badge_and_popup);
        if (packageName != null) {
            Set<String> set = this.mCacheComponentsMap.get(packageName);
            if (set != null) {
                return set;
            }
            PackageManager packageManager = this.mContext.getPackageManager();
            Intent intent = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN, (Uri) null);
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.setPackage(packageName);
            Iterator<ResolveInfo> it = packageManager.queryIntentActivities(intent, 0).iterator();
            while (it.hasNext()) {
                String str = it.next().activityInfo.name;
                int length = stringArray.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        z = true;
                        break;
                    }
                    if (str.equals(stringArray[i])) {
                        z = false;
                        break;
                    }
                    i++;
                }
                if (z) {
                    hashSet.add(str);
                }
            }
        }
        this.mCacheComponentsMap.put(packageName, hashSet);
        return hashSet;
    }

    public void updateNotificationBadge(PackageUserKey mPackageUserKey, boolean isNotiBadge) {
        Set<String> componetsFromPackageName = getComponetsFromPackageName(mPackageUserKey.mPackageName);
        Iterator<String> it = componetsFromPackageName.iterator();
        while (it.hasNext()) {
            AppNotifierData appNotifierData = new AppNotifierData(mPackageUserKey.mPackageName, it.next(), mPackageUserKey.mUser);
            if (isNotiBadge && !this.mNotiComponents.contains(appNotifierData)) {
                this.mNotiComponents.add(appNotifierData);
            } else if (!isNotiBadge && this.mNotiComponents.contains(appNotifierData)) {
                this.mNotiComponents.remove(appNotifierData);
            }
            BadgeInfo badgeInfo = this.mPackageUserToBadgeInfos.get(mPackageUserKey);
            int notificationCount = 0;
            if (badgeInfo != null) {
                notificationCount = badgeInfo.getNotificationCount();
            }
            notifyCount(appNotifierData, notificationCount);
            notifyGroupCount(appNotifierData, notificationCount);
        }
        updateSecretModeIconforNumberBadge(this.mContext, "jp.naver.line.android");
        this.mComponents.addAll(componetsFromPackageName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNotificationBadgeStatus() {
        notifyAllCount();
        notifyAllGroupCount();
    }

    public void clearNotiComponents() {
        this.mNotiComponents.clear();
    }

    public boolean isShowBadgeNumber() {
        return this.isShowNumber == 0;
    }

    private static class ActionSpec {
        private String mAction;
        private HashSet<String> mComponents;
        private HashMap<String, ExtraSpec> mExtraSpecs;

        public ActionSpec(String action, HashMap<String, ExtraSpec> extraSpecs, HashSet<String> components) {
            this.mAction = new String(action);
            this.mExtraSpecs = new HashMap<>(extraSpecs);
            setComponents(new HashSet<>(components));
        }

        public String getAction() {
            return this.mAction;
        }

        public Collection<ExtraSpec> getExtraSpecs() {
            return this.mExtraSpecs.values();
        }

        public HashSet<String> getComponents() {
            return this.mComponents;
        }

        public void setComponents(HashSet<String> mComponents) {
            this.mComponents = mComponents;
        }
    }

    private static class ExtraSpec {
        public static final String USAGE_COMPONENT = "component";
        public static final String USAGE_MAIN = "main";
        public static final String USAGE_PACKAGE = "package";
        private String mName;
        private String mUsage;

        public ExtraSpec(String name, String usage) {
            Objects.requireNonNull(name);
            this.mName = new String(name);
            this.mUsage = new String(usage);
        }

        public String getName() {
            return this.mName;
        }

        public String getUsage() {
            return this.mUsage;
        }
    }
}
