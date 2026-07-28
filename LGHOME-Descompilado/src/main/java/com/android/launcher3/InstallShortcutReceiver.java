package com.android.launcher3;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.os.Parcelable;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.util.Provider;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.memory.MemoryUtils;
import com.lge.launcher3.smartbulletin.provider.SBContract;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

/* JADX INFO: loaded from: classes.dex */
public class InstallShortcutReceiver extends BroadcastReceiver {
    private static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    private static final String APPS_PENDING_INSTALL = "apps_to_install";
    private static final String APPS_PENDING_INSTALL_SWIVEL = "apps_to_install_swivel";
    private static final String APP_SHORTCUT_TYPE_KEY = "isAppShortcut";
    private static final String APP_WIDGET_TYPE_KEY = "isAppWidget";
    private static final boolean DBG = false;
    private static final String DEEPSHORTCUT_TYPE_KEY = "isDeepShortcut";
    public static final int FLAG_ACTIVITY_PAUSED = 1;
    public static final int FLAG_BULK_ADD = 4;
    public static final int FLAG_DRAG_AND_DROP = 4;
    public static final int FLAG_LOADER_RUNNING = 2;
    private static final String ICON_KEY = "icon";
    private static final String ICON_RESOURCE_NAME_KEY = "iconResource";
    private static final String ICON_RESOURCE_PACKAGE_NAME_KEY = "iconResourcePackage";
    private static final String LAUNCH_INTENT_KEY = "intent.launch";
    private static final String NAME_KEY = "name";
    public static final int NEW_SHORTCUT_BOUNCE_DURATION = 450;
    public static final int NEW_SHORTCUT_STAGGER_DELAY = 85;
    private static final String TAG = "InstallShortcutReceiver";
    private static final String USER_HANDLE_KEY = "userHandle";
    private static boolean mUseInstallQueue;
    private static boolean mblockInstallQueueSwivel;
    private static final Object sLock = new Object();

    private static void addToInstallQueue(SharedPreferences sharedPrefs, PendingInstallShortcutInfo info) {
        synchronized (sLock) {
            String strEncodeToString = info.encodeToString();
            if (strEncodeToString != null) {
                Set<String> stringSet = sharedPrefs.getStringSet(APPS_PENDING_INSTALL, null);
                HashSet hashSet = stringSet != null ? new HashSet(stringSet) : new HashSet(1);
                hashSet.add(strEncodeToString);
                sharedPrefs.edit().putStringSet(APPS_PENDING_INSTALL, hashSet).apply();
            }
        }
    }

    private static void addToInstallQueueSwivel(SharedPreferences sharedPrefs, PendingInstallShortcutInfo info) {
        synchronized (sLock) {
            String strEncodeToString = info.encodeToString();
            if (strEncodeToString != null) {
                Set<String> stringSet = sharedPrefs.getStringSet(APPS_PENDING_INSTALL_SWIVEL, null);
                HashSet hashSet = stringSet != null ? new HashSet(stringSet) : new HashSet(1);
                hashSet.add(strEncodeToString);
                sharedPrefs.edit().putStringSet(APPS_PENDING_INSTALL_SWIVEL, hashSet).apply();
            }
        }
    }

    public static void removeFromInstallQueue(Context context, HashSet<String> packageNames, UserHandle user) {
        if (packageNames.isEmpty()) {
            return;
        }
        SharedPreferences prefs = Utilities.getPrefs(context);
        synchronized (sLock) {
            Set<String> stringSet = prefs.getStringSet(APPS_PENDING_INSTALL, null);
            if (Utilities.isEmpty(stringSet)) {
                return;
            }
            HashSet hashSet = new HashSet(stringSet);
            Iterator<String> it = hashSet.iterator();
            while (it.hasNext()) {
                try {
                    Decoder decoder = new Decoder(it.next(), context);
                    if (packageNames.contains(getIntentPackage(decoder.launcherIntent)) && user.equals(decoder.user)) {
                        it.remove();
                    }
                } catch (URISyntaxException | JSONException e) {
                    Log.d(TAG, "Exception reading shortcut to add: " + e);
                    it.remove();
                }
            }
            prefs.edit().putStringSet(APPS_PENDING_INSTALL, hashSet).apply();
        }
    }

    private static ArrayList<PendingInstallShortcutInfo> getAndClearInstallQueue(SharedPreferences sharedPrefs, Context context) {
        synchronized (sLock) {
            Set<String> stringSet = sharedPrefs.getStringSet(APPS_PENDING_INSTALL, null);
            if (stringSet == null) {
                return new ArrayList<>();
            }
            ArrayList<PendingInstallShortcutInfo> arrayList = new ArrayList<>();
            Iterator<String> it = stringSet.iterator();
            while (it.hasNext()) {
                PendingInstallShortcutInfo pendingInstallShortcutInfoDecode = decode(it.next(), context);
                if (pendingInstallShortcutInfoDecode != null) {
                    arrayList.add(pendingInstallShortcutInfoDecode);
                }
            }
            sharedPrefs.edit().putStringSet(APPS_PENDING_INSTALL, new HashSet()).commit();
            return arrayList;
        }
    }

    private static ArrayList<PendingInstallShortcutInfo> getAndClearInstallQueueSwivel(SharedPreferences sharedPrefs, Context context) {
        synchronized (sLock) {
            Set<String> stringSet = sharedPrefs.getStringSet(APPS_PENDING_INSTALL_SWIVEL, null);
            if (stringSet == null) {
                return new ArrayList<>();
            }
            ArrayList<PendingInstallShortcutInfo> arrayList = new ArrayList<>();
            Iterator<String> it = stringSet.iterator();
            while (it.hasNext()) {
                PendingInstallShortcutInfo pendingInstallShortcutInfoDecode = decode(it.next(), context);
                if (pendingInstallShortcutInfoDecode != null) {
                    arrayList.add(pendingInstallShortcutInfoDecode);
                }
            }
            sharedPrefs.edit().putStringSet(APPS_PENDING_INSTALL_SWIVEL, new HashSet()).commit();
            return arrayList;
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent data) {
        PendingInstallShortcutInfo pendingInstallShortcutInfoCreatePendingInfo;
        LGLog.d(TAG, String.format("InstallShortcutReceiver.onReceive() : %s{%s}", data.getStringExtra("android.intent.extra.shortcut.NAME"), ((Intent) data.getParcelableExtra("android.intent.extra.shortcut.INTENT")).getComponent()));
        if (!MemoryUtils.hasAvailableFileSystemMemory(null, false)) {
            LGLog.i(TAG, "Memory is full. so InstallShortcutReceiver.onReceive() is canceled.");
            return;
        }
        if (ACTION_INSTALL_SHORTCUT.equals(data.getAction()) && (pendingInstallShortcutInfoCreatePendingInfo = createPendingInfo(context, data)) != null) {
            if (!pendingInstallShortcutInfoCreatePendingInfo.isLauncherActivity() && !PackageManagerHelper.hasPermissionForActivity(context, pendingInstallShortcutInfoCreatePendingInfo.launchIntent, null)) {
                Log.e(TAG, "Ignoring malicious intent " + pendingInstallShortcutInfoCreatePendingInfo.launchIntent.toUri(0));
                return;
            }
            queuePendingShortcutInfo(pendingInstallShortcutInfoCreatePendingInfo, context);
        }
    }

    private static boolean isValidExtraType(Intent intent, String key, Class type) {
        Parcelable parcelableExtra = intent.getParcelableExtra(key);
        return parcelableExtra == null || type.isInstance(parcelableExtra);
    }

    private static PendingInstallShortcutInfo createPendingInfo(Context context, Intent data) {
        if (isValidExtraType(data, "android.intent.extra.shortcut.INTENT", Intent.class) && isValidExtraType(data, "android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.class) && isValidExtraType(data, "android.intent.extra.shortcut.ICON", Bitmap.class)) {
            PendingInstallShortcutInfo pendingInstallShortcutInfo = new PendingInstallShortcutInfo(data, Process.myUserHandle(), context);
            if (pendingInstallShortcutInfo.launchIntent != null && pendingInstallShortcutInfo.label != null) {
                return convertToLauncherActivityIfPossible(pendingInstallShortcutInfo);
            }
        }
        return null;
    }

    public static ShortcutInfo fromShortcutIntent(Context context, Intent data) {
        PendingInstallShortcutInfo pendingInstallShortcutInfoCreatePendingInfo = createPendingInfo(context, data);
        if (!LGHomeFeature.isEnableDefaultHome() && pendingInstallShortcutInfoCreatePendingInfo != null) {
            pendingInstallShortcutInfoCreatePendingInfo = convertToLauncherActivityIfPossible(pendingInstallShortcutInfoCreatePendingInfo);
        }
        if (pendingInstallShortcutInfoCreatePendingInfo == null) {
            return null;
        }
        return (ShortcutInfo) pendingInstallShortcutInfoCreatePendingInfo.getItemInfo();
    }

    public static void queueShortcut(ShortcutInfoCompat info, Context context) {
        queuePendingShortcutInfo(new PendingInstallShortcutInfo(info, context), context);
    }

    public static void queueShortcutSwivel(ShortcutInfoCompat info, Context context) {
        queuePendingShortcutInfoSwivel(new PendingInstallShortcutInfo(info, context), context);
    }

    public static void queueWidget(AppWidgetProviderInfo info, int widgetId, Context context) {
        queuePendingShortcutInfo(new PendingInstallShortcutInfo(info, widgetId, context), context);
    }

    public static void queueActivityInfo(LauncherActivityInfo activity, Context context) {
        queuePendingShortcutInfo(new PendingInstallShortcutInfo(activity, context), context);
    }

    static void queueInstallShortcut(LauncherActivityInfo info, Context context) {
        queuePendingShortcutInfo(new PendingInstallShortcutInfo(info, context), context);
    }

    private static void queuePendingShortcutInfo(PendingInstallShortcutInfo info, Context context) {
        if (LGHomeFeature.isEnableDefaultHome() && info.isLauncherActivity()) {
            return;
        }
        boolean z = LauncherAppState.getInstance(context).getModel().getCallback() == null;
        addToInstallQueue(context.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0), info);
        if (mUseInstallQueue || z) {
            return;
        }
        flushInstallQueue(context);
    }

    private static void queuePendingShortcutInfoSwivel(PendingInstallShortcutInfo info, Context context) {
        LauncherAppState launcherAppState = LauncherAppState.getInstance(context);
        addToInstallQueueSwivel(context.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0), info);
        if (mblockInstallQueueSwivel || launcherAppState.getModel().getCallback() == null) {
            return;
        }
        flushInstallQueueSwivel(context);
    }

    public static HashSet<ShortcutKey> getPendingShortcuts(Context context) {
        HashSet<ShortcutKey> hashSet = new HashSet<>();
        Set<String> stringSet = Utilities.getPrefs(context).getStringSet(APPS_PENDING_INSTALL, null);
        if (Utilities.isEmpty(stringSet)) {
            return hashSet;
        }
        Iterator<String> it = stringSet.iterator();
        while (it.hasNext()) {
            try {
                Decoder decoder = new Decoder(it.next(), context);
                if (decoder.optBoolean(DEEPSHORTCUT_TYPE_KEY)) {
                    hashSet.add(ShortcutKey.fromIntent(decoder.launcherIntent, decoder.user));
                }
            } catch (URISyntaxException | JSONException e) {
                Log.d(TAG, "Exception reading shortcut to add: " + e);
            }
        }
        return hashSet;
    }

    static void enableInstallQueue() {
        mUseInstallQueue = true;
    }

    static void disableAndFlushInstallQueue(Context context) {
        mUseInstallQueue = false;
        flushInstallQueue(context);
    }

    static void flushInstallQueue(Context context) {
        ArrayList<PendingInstallShortcutInfo> andClearInstallQueue = getAndClearInstallQueue(context.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0), context);
        if (andClearInstallQueue.isEmpty()) {
            return;
        }
        ArrayList<? extends ItemInfo> arrayList = new ArrayList<>();
        for (PendingInstallShortcutInfo pendingInstallShortcutInfo : andClearInstallQueue) {
            Intent intent = pendingInstallShortcutInfo.launchIntent;
            String targetPackage = pendingInstallShortcutInfo.getTargetPackage();
            if (TextUtils.isEmpty(targetPackage) || LauncherModel.isValidPackage(context, targetPackage, Process.myUserHandle())) {
                arrayList.add(pendingInstallShortcutInfo.getItemInfo());
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        LauncherAppState.getInstance(context).getModel().addAndBindAddedWorkspaceItems(context, arrayList);
    }

    static void blockInstallQueueSwivel() {
        mblockInstallQueueSwivel = true;
    }

    static void unblockAndFlushInstallQueueSwivel(Context context) {
        mblockInstallQueueSwivel = false;
        flushInstallQueueSwivel(context);
    }

    static void flushInstallQueueSwivel(Context context) {
        ArrayList<PendingInstallShortcutInfo> andClearInstallQueueSwivel = getAndClearInstallQueueSwivel(context.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0), context);
        if (andClearInstallQueueSwivel.isEmpty()) {
            return;
        }
        ArrayList<? extends ItemInfo> arrayList = new ArrayList<>();
        for (PendingInstallShortcutInfo pendingInstallShortcutInfo : andClearInstallQueueSwivel) {
            Intent intent = pendingInstallShortcutInfo.launchIntent;
            String targetPackage = pendingInstallShortcutInfo.getTargetPackage();
            if (TextUtils.isEmpty(targetPackage) || LauncherModel.isValidPackage(context, targetPackage, Process.myUserHandle())) {
                arrayList.add(pendingInstallShortcutInfo.getItemInfo());
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        LauncherAppState.getInstance(context).getModel().addItemsOnSwivelHome(arrayList);
    }

    static CharSequence ensureValidName(Context context, Intent intent, CharSequence name) {
        if (name != null) {
            return name;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            return packageManager.getActivityInfo(intent.getComponent(), 0).loadLabel(packageManager);
        } catch (PackageManager.NameNotFoundException unused) {
            return "";
        }
    }

    private static class PendingInstallShortcutInfo {
        final LauncherActivityInfo activityInfo;
        final Intent data;
        final String label;
        final Intent launchIntent;
        final Context mContext;
        final AppWidgetProviderInfo providerInfo;
        final ShortcutInfoCompat shortcutInfo;
        final UserHandle user;

        public PendingInstallShortcutInfo(Intent data, UserHandle user, Context context) {
            this.activityInfo = null;
            this.shortcutInfo = null;
            this.providerInfo = null;
            this.data = data;
            this.user = user;
            this.mContext = context;
            this.launchIntent = (Intent) data.getParcelableExtra("android.intent.extra.shortcut.INTENT");
            this.label = data.getStringExtra("android.intent.extra.shortcut.NAME");
        }

        public PendingInstallShortcutInfo(LauncherActivityInfo info, Context context) {
            this.activityInfo = info;
            this.shortcutInfo = null;
            this.providerInfo = null;
            this.data = null;
            this.user = info.getUser();
            this.mContext = context;
            this.launchIntent = AppInfo.makeLaunchIntent(info);
            this.label = info.getLabel().toString();
        }

        public PendingInstallShortcutInfo(ShortcutInfoCompat info, Context context) {
            this.activityInfo = null;
            this.shortcutInfo = info;
            this.providerInfo = null;
            this.data = null;
            this.mContext = context;
            this.user = info.getUserHandle();
            this.launchIntent = info.makeIntent(context);
            this.label = info.getShortLabel().toString();
        }

        public PendingInstallShortcutInfo(AppWidgetProviderInfo info, int widgetId, Context context) {
            this.activityInfo = null;
            this.shortcutInfo = null;
            this.providerInfo = info;
            this.data = null;
            this.mContext = context;
            this.user = info.getProfile();
            this.launchIntent = new Intent().setComponent(info.provider).putExtra("appWidgetId", widgetId);
            this.label = info.label;
        }

        public String encodeToString() {
            try {
                if (this.activityInfo != null) {
                    return new JSONStringer().object().key(InstallShortcutReceiver.LAUNCH_INTENT_KEY).value(this.launchIntent.toUri(0)).key(InstallShortcutReceiver.APP_SHORTCUT_TYPE_KEY).value(true).key(InstallShortcutReceiver.USER_HANDLE_KEY).value(UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(this.user)).key(SBContract.SmartBulletin.NOTI_TIME).value(System.currentTimeMillis()).endObject().toString();
                }
                if (this.shortcutInfo != null) {
                    return new JSONStringer().object().key(InstallShortcutReceiver.LAUNCH_INTENT_KEY).value(this.launchIntent.toUri(0)).key(InstallShortcutReceiver.DEEPSHORTCUT_TYPE_KEY).value(true).key(InstallShortcutReceiver.USER_HANDLE_KEY).value(UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(this.user)).endObject().toString();
                }
                if (this.providerInfo != null) {
                    return new JSONStringer().object().key(InstallShortcutReceiver.LAUNCH_INTENT_KEY).value(this.launchIntent.toUri(0)).key(InstallShortcutReceiver.APP_WIDGET_TYPE_KEY).value(true).key(InstallShortcutReceiver.USER_HANDLE_KEY).value(UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(this.user)).endObject().toString();
                }
                if (this.launchIntent.getAction() == null) {
                    this.launchIntent.setAction("android.intent.action.VIEW");
                } else if (this.launchIntent.getAction().equals(PackageUtils.ANDROID_INTENT_ACTION_MAIN) && this.launchIntent.getCategories() != null && this.launchIntent.getCategories().contains("android.intent.category.LAUNCHER")) {
                    this.launchIntent.addFlags(270532608);
                }
                String string = InstallShortcutReceiver.ensureValidName(this.mContext, this.launchIntent, this.label).toString();
                Bitmap bitmap = (Bitmap) this.data.getParcelableExtra("android.intent.extra.shortcut.ICON");
                Intent.ShortcutIconResource shortcutIconResource = (Intent.ShortcutIconResource) this.data.getParcelableExtra("android.intent.extra.shortcut.ICON_RESOURCE");
                boolean booleanExtra = this.data.getBooleanExtra(LauncherConst.EXTRA_NO_ICON_FRAMES, false);
                JSONStringer jSONStringerValue = new JSONStringer().object().key(InstallShortcutReceiver.LAUNCH_INTENT_KEY).value(this.launchIntent.toUri(0)).key(InstallShortcutReceiver.NAME_KEY).value(string);
                if (bitmap != null) {
                    byte[] bArrFlattenBitmap = Utilities.flattenBitmap(bitmap);
                    jSONStringerValue = jSONStringerValue.key("icon").value(Base64.encodeToString(bArrFlattenBitmap, 0, bArrFlattenBitmap.length, 0));
                }
                if (shortcutIconResource != null) {
                    jSONStringerValue = jSONStringerValue.key("iconResource").value(shortcutIconResource.resourceName).key(InstallShortcutReceiver.ICON_RESOURCE_PACKAGE_NAME_KEY).value(shortcutIconResource.packageName);
                }
                jSONStringerValue.key(LauncherConst.EXTRA_NO_ICON_FRAMES).value(booleanExtra);
                return jSONStringerValue.endObject().toString();
            } catch (JSONException e) {
                Log.d(InstallShortcutReceiver.TAG, "Exception when adding shortcut: " + e);
                return null;
            }
        }

        public ItemInfo getItemInfo() {
            if (this.activityInfo != null) {
                AppInfo appInfo = new AppInfo(this.mContext, this.activityInfo, this.user);
                final LauncherAppState launcherAppState = LauncherAppState.getInstance(this.mContext);
                appInfo.title = "";
                appInfo.iconBitmap = launcherAppState.getIconCache().getDefaultIcon(this.user);
                final ShortcutInfo shortcutInfoMakeShortcut = appInfo.makeShortcut();
                if (Looper.myLooper() == LauncherModel.getWorkerLooper()) {
                    launcherAppState.getIconCache().getTitleAndIcon(shortcutInfoMakeShortcut, this.activityInfo, false);
                } else {
                    launcherAppState.getModel().updateAndBindShortcutInfo(new Provider<ShortcutInfo>() { // from class: com.android.launcher3.InstallShortcutReceiver.PendingInstallShortcutInfo.1
                        /* JADX DEBUG: Method merged with bridge method: get()Ljava/lang/Object; */
                        /* JADX WARN: Can't rename method to resolve collision */
                        @Override // com.android.launcher3.util.Provider
                        public ShortcutInfo get() {
                            launcherAppState.getIconCache().getTitleAndIcon(shortcutInfoMakeShortcut, PendingInstallShortcutInfo.this.activityInfo, false);
                            return shortcutInfoMakeShortcut;
                        }
                    });
                }
                return shortcutInfoMakeShortcut;
            }
            if (this.shortcutInfo != null) {
                ShortcutInfo shortcutInfo = new ShortcutInfo(this.shortcutInfo, this.mContext);
                shortcutInfo.iconBitmap = LauncherIcons.createShortcutIcon(this.shortcutInfo, this.mContext);
                return shortcutInfo;
            }
            AppWidgetProviderInfo appWidgetProviderInfo = this.providerInfo;
            if (appWidgetProviderInfo != null) {
                LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfoFromProviderInfo = LauncherAppWidgetProviderInfo.fromProviderInfo(this.mContext, appWidgetProviderInfo);
                LauncherAppWidgetInfo launcherAppWidgetInfo = new LauncherAppWidgetInfo(this.launchIntent.getIntExtra("appWidgetId", 0), launcherAppWidgetProviderInfoFromProviderInfo.provider);
                InvariantDeviceProfile idp = LauncherAppState.getIDP(this.mContext);
                launcherAppWidgetInfo.minSpanX = launcherAppWidgetProviderInfoFromProviderInfo.minSpanX;
                launcherAppWidgetInfo.minSpanY = launcherAppWidgetProviderInfoFromProviderInfo.minSpanY;
                launcherAppWidgetInfo.spanX = Math.min(launcherAppWidgetProviderInfoFromProviderInfo.spanX, idp.numColumns);
                launcherAppWidgetInfo.spanY = Math.min(launcherAppWidgetProviderInfoFromProviderInfo.spanY, idp.numRows);
                launcherAppWidgetInfo.maxSpanX = launcherAppWidgetProviderInfoFromProviderInfo.maxSpanX;
                launcherAppWidgetInfo.maxSpanY = launcherAppWidgetProviderInfoFromProviderInfo.maxSpanY;
                return launcherAppWidgetInfo;
            }
            return InstallShortcutReceiver.createShortcutInfo(this.data, LauncherAppState.getInstance(this.mContext));
        }

        public ShortcutInfo getShortcutInfo() {
            LauncherActivityInfo launcherActivityInfo = this.activityInfo;
            if (launcherActivityInfo != null) {
                return ShortcutInfo.fromActivityInfo(launcherActivityInfo, this.mContext);
            }
            return LauncherAppState.getInstance(this.mContext).getModel().infoFromShortcutIntent(this.mContext, this.data);
        }

        public String getTargetPackage() {
            String str = this.launchIntent.getPackage();
            if (str != null) {
                return str;
            }
            if (this.launchIntent.getComponent() == null) {
                return null;
            }
            return this.launchIntent.getComponent().getPackageName();
        }

        public boolean isLauncherActivity() {
            return this.activityInfo != null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getIntentPackage(Intent intent) {
        return intent.getComponent() == null ? intent.getPackage() : intent.getComponent().getPackageName();
    }

    private static PendingInstallShortcutInfo decode(String encoded, Context context) {
        try {
            Decoder decoder = new Decoder(encoded, context);
            if (decoder.optBoolean(APP_SHORTCUT_TYPE_KEY)) {
                LauncherActivityInfo launcherActivityInfoResolveActivity = LauncherAppsCompat.getInstance(context).resolveActivity(decoder.launcherIntent, decoder.user);
                if (launcherActivityInfoResolveActivity == null) {
                    return null;
                }
                return new PendingInstallShortcutInfo(launcherActivityInfoResolveActivity, context);
            }
            if (decoder.optBoolean(DEEPSHORTCUT_TYPE_KEY)) {
                List<ShortcutInfoCompat> listQueryForFullDetails = DeepShortcutManager.getInstance(context).queryForFullDetails(decoder.launcherIntent.getPackage(), Arrays.asList(decoder.launcherIntent.getStringExtra("shortcut_id")), decoder.user);
                if (listQueryForFullDetails.isEmpty()) {
                    return null;
                }
                return new PendingInstallShortcutInfo(listQueryForFullDetails.get(0), context);
            }
            if (decoder.optBoolean(APP_WIDGET_TYPE_KEY)) {
                int intExtra = decoder.launcherIntent.getIntExtra("appWidgetId", 0);
                AppWidgetProviderInfo appWidgetInfo = AppWidgetManager.getInstance(context).getAppWidgetInfo(intExtra);
                if (appWidgetInfo != null && appWidgetInfo.provider.equals(decoder.launcherIntent.getComponent()) && appWidgetInfo.getProfile().equals(decoder.user)) {
                    return new PendingInstallShortcutInfo(appWidgetInfo, intExtra, context);
                }
                return null;
            }
            Intent intent = new Intent();
            intent.putExtra("android.intent.extra.shortcut.INTENT", decoder.launcherIntent);
            intent.putExtra("android.intent.extra.shortcut.NAME", decoder.getString(NAME_KEY));
            String strOptString = decoder.optString("icon");
            String strOptString2 = decoder.optString("iconResource");
            String strOptString3 = decoder.optString(ICON_RESOURCE_PACKAGE_NAME_KEY);
            if (strOptString != null && !strOptString.isEmpty()) {
                byte[] bArrDecode = Base64.decode(strOptString, 0);
                intent.putExtra("android.intent.extra.shortcut.ICON", BitmapFactory.decodeByteArray(bArrDecode, 0, bArrDecode.length));
            } else if (strOptString2 != null && !strOptString2.isEmpty()) {
                Intent.ShortcutIconResource shortcutIconResource = new Intent.ShortcutIconResource();
                shortcutIconResource.resourceName = strOptString2;
                shortcutIconResource.packageName = strOptString3;
                intent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", shortcutIconResource);
            }
            intent.putExtra(LauncherConst.EXTRA_NO_ICON_FRAMES, decoder.optBoolean(LauncherConst.EXTRA_NO_ICON_FRAMES));
            return new PendingInstallShortcutInfo(intent, decoder.user, context);
        } catch (URISyntaxException | JSONException e) {
            Log.d(TAG, "Exception reading shortcut to add: " + e);
            return null;
        }
    }

    private static class Decoder extends JSONObject {
        public final Intent launcherIntent;
        public final UserHandle user;

        private Decoder(String encoded, Context context) throws JSONException, URISyntaxException {
            UserHandle userHandleMyUserHandle;
            super(encoded);
            this.launcherIntent = Intent.parseUri(getString(InstallShortcutReceiver.LAUNCH_INTENT_KEY), 0);
            if (has(InstallShortcutReceiver.USER_HANDLE_KEY)) {
                userHandleMyUserHandle = UserManagerCompat.getInstance(context).getUserForSerialNumber(getLong(InstallShortcutReceiver.USER_HANDLE_KEY));
            } else {
                userHandleMyUserHandle = Process.myUserHandle();
            }
            this.user = userHandleMyUserHandle;
            if (userHandleMyUserHandle == null) {
                throw new JSONException("Invalid user");
            }
        }
    }

    private static PendingInstallShortcutInfo convertToLauncherActivityIfPossible(PendingInstallShortcutInfo original) {
        LauncherActivityInfo launcherActivityInfoResolveActivity;
        return (original.isLauncherActivity() || !Utilities.isLauncherAppTarget(original.launchIntent) || (launcherActivityInfoResolveActivity = LauncherAppsCompat.getInstance(original.mContext).resolveActivity(original.launchIntent, original.user)) == null) ? original : new PendingInstallShortcutInfo(launcherActivityInfoResolveActivity, original.mContext);
    }

    private static class LazyShortcutsProvider extends Provider<List<ItemInfo>> {
        private final Context mContext;
        private final ArrayList<PendingInstallShortcutInfo> mPendingItems;

        public LazyShortcutsProvider(Context context, ArrayList<PendingInstallShortcutInfo> items) {
            this.mContext = context;
            this.mPendingItems = items;
        }

        /* JADX DEBUG: Method merged with bridge method: get()Ljava/lang/Object; */
        /* JADX DEBUG: Return type fixed from 'java.util.ArrayList<com.android.launcher3.model.data.ItemInfo>' to match base method */
        @Override // com.android.launcher3.util.Provider
        public List<ItemInfo> get() {
            Preconditions.assertNonUiThread();
            ArrayList arrayList = new ArrayList();
            LauncherAppsCompat launcherAppsCompat = LauncherAppsCompat.getInstance(this.mContext);
            for (PendingInstallShortcutInfo pendingInstallShortcutInfo : this.mPendingItems) {
                String intentPackage = InstallShortcutReceiver.getIntentPackage(pendingInstallShortcutInfo.launchIntent);
                if (TextUtils.isEmpty(intentPackage) || launcherAppsCompat.isPackageEnabledForProfile(intentPackage, pendingInstallShortcutInfo.user)) {
                    arrayList.add(pendingInstallShortcutInfo.getItemInfo());
                }
            }
            return arrayList;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ShortcutInfo createShortcutInfo(Intent data, LauncherAppState app) {
        Intent intent = (Intent) data.getParcelableExtra("android.intent.extra.shortcut.INTENT");
        String stringExtra = data.getStringExtra("android.intent.extra.shortcut.NAME");
        Parcelable parcelableExtra = data.getParcelableExtra("android.intent.extra.shortcut.ICON");
        if (intent == null) {
            Log.e(TAG, "Can't construct ShorcutInfo with null intent");
            return null;
        }
        ShortcutInfo shortcutInfo = new ShortcutInfo();
        shortcutInfo.user = Process.myUserHandle();
        if (parcelableExtra instanceof Bitmap) {
            shortcutInfo.iconBitmap = LauncherIcons.createIconBitmap((Bitmap) parcelableExtra, app.getContext());
        } else {
            Parcelable parcelableExtra2 = data.getParcelableExtra("android.intent.extra.shortcut.ICON_RESOURCE");
            if (parcelableExtra2 instanceof Intent.ShortcutIconResource) {
                shortcutInfo.iconResource = (Intent.ShortcutIconResource) parcelableExtra2;
                shortcutInfo.iconBitmap = LauncherIcons.createIconBitmap(shortcutInfo.iconResource, app.getContext());
            }
        }
        if (shortcutInfo.iconBitmap == null) {
            shortcutInfo.iconBitmap = app.getIconCache().getDefaultIcon(shortcutInfo.user);
        }
        shortcutInfo.title = Utilities.trim(stringExtra);
        shortcutInfo.contentDescription = UserManagerCompat.getInstance(app.getContext()).getBadgedLabelForUser(shortcutInfo.title, shortcutInfo.user);
        shortcutInfo.intent = intent;
        return shortcutInfo;
    }
}
