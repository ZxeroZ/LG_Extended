package com.android.launcher3.model;

import android.app.RemoteAction;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.popup.RemoteActionShortcut;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.util.SimpleBroadcastReceiver;
import com.lge.launcher3.R;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

/* JADX INFO: loaded from: classes.dex */
public final class WellbeingModel {
    private static final boolean DEBUG = false;
    private static final String EXTRA_ACTION = "action";
    private static final String EXTRA_ACTIONS = "actions";
    private static final String EXTRA_MAX_NUM_ACTIONS_SHOWN = "max_num_actions_shown";
    private static final String EXTRA_PACKAGES = "packages";
    private static final String EXTRA_SUCCESS = "success";
    private static final String METHOD_GET_ACTIONS = "get_actions";
    private static final int MSG_FULL_REFRESH = 3;
    private static final int MSG_PACKAGE_ADDED = 1;
    private static final int MSG_PACKAGE_REMOVED = 2;
    private static final String TAG = "WellbeingModel";
    private final ContentObserver mContentObserver;
    private final Context mContext;
    private boolean mIsInTest;
    private final String mWellbeingProviderPkg;
    private static final int[] RETRY_TIMES_MS = {5000, 15000, 30000};
    public static final MainThreadInitializedObject<WellbeingModel> INSTANCE = new MainThreadInitializedObject<>(new MainThreadInitializedObject.ObjectProvider() { // from class: com.android.launcher3.model.-$$Lambda$WellbeingModel$NamGAZkV_nPfIPATzPfJ7OZ7BRk
        @Override // com.android.launcher3.util.MainThreadInitializedObject.ObjectProvider
        public final Object get(Context context) {
            return WellbeingModel.lambda$NamGAZkV_nPfIPATzPfJ7OZ7BRk(context);
        }
    });
    public static final SystemShortcut.Factory SHORTCUT_FACTORY = new SystemShortcut.Factory() { // from class: com.android.launcher3.model.-$$Lambda$WellbeingModel$d91tWAU9JaZzOY9SW9KzSw2mboo
        @Override // com.android.launcher3.popup.SystemShortcut.Factory
        public final SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, ItemInfo itemInfo) {
            return WellbeingModel.lambda$static$2(baseDraggingActivity, itemInfo);
        }
    };
    private final Object mModelLock = new Object();
    private final Map<String, RemoteAction> mActionIdMap = new ArrayMap();
    private final Map<String, String> mPackageToActionId = new HashMap();
    private final Handler mWorkerHandler = new Handler(Executors.createAndStartNewLooper("WellbeingHandler"), new Handler.Callback() { // from class: com.android.launcher3.model.-$$Lambda$WellbeingModel$1w3sAdwAZ1LqK0UB_LaC7ATIPAI
        @Override // android.os.Handler.Callback
        public final boolean handleMessage(Message message) {
            return this.f$0.handleMessage(message);
        }
    });

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0002: CONSTRUCTOR (r1v0 android.content.Context) A[MD:(android.content.Context):void (m)] call: com.android.launcher3.model.WellbeingModel.<init>(android.content.Context):void type: CONSTRUCTOR */
    public static /* synthetic */ WellbeingModel lambda$NamGAZkV_nPfIPATzPfJ7OZ7BRk(Context context) {
        return new WellbeingModel(context);
    }

    private WellbeingModel(final Context context) {
        this.mContext = context;
        String string = context.getString(R.string.wellbeing_provider_pkg);
        this.mWellbeingProviderPkg = string;
        this.mContentObserver = new ContentObserver(Executors.MAIN_EXECUTOR.getHandler()) { // from class: com.android.launcher3.model.WellbeingModel.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                if (WellbeingModel.this.mIsInTest) {
                    Log.d(WellbeingModel.TAG, "ContentObserver.onChange() called with: selfChange = [" + selfChange + "], uri = [" + uri + "]");
                }
                Preconditions.assertUIThread();
                WellbeingModel.this.updateWellbeingData();
            }
        };
        if (TextUtils.isEmpty(string)) {
            return;
        }
        context.registerReceiver(new SimpleBroadcastReceiver(new Consumer() { // from class: com.android.launcher3.model.-$$Lambda$_Q0nrYKmJGd5O39Xk7yQDjLnKko
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.onWellbeingProviderChanged((Intent) obj);
            }
        }), PackageManagerHelper.getPackageFilter(string, "android.intent.action.PACKAGE_ADDED", "android.intent.action.PACKAGE_CHANGED", "android.intent.action.PACKAGE_REMOVED", "android.intent.action.PACKAGE_DATA_CLEARED", "android.intent.action.PACKAGE_RESTARTED"));
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme(AppNotifierManager.ExtraSpec.USAGE_PACKAGE);
        context.registerReceiver(new SimpleBroadcastReceiver(new Consumer() { // from class: com.android.launcher3.model.-$$Lambda$WellbeingModel$BmqsdCTQ8xqvNH4paAebTdVMW2I
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.onAppPackageChanged((Intent) obj);
            }
        }), intentFilter);
        restartObserver();
    }

    public void setInTest(boolean inTest) {
        this.mIsInTest = inTest;
    }

    protected void onWellbeingProviderChanged(Intent intent) {
        if (this.mIsInTest) {
            Log.d(TAG, "Changes to Wellbeing package: intent = [" + intent + "]");
        }
        restartObserver();
    }

    private void restartObserver() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        contentResolver.unregisterContentObserver(this.mContentObserver);
        Uri uriBuild = apiBuilder().path(EXTRA_ACTIONS).build();
        try {
            contentResolver.registerContentObserver(uriBuild, true, this.mContentObserver);
        } catch (Exception e) {
            Log.e(TAG, "Failed to register content observer for " + uriBuild + ": " + e);
            if (this.mIsInTest) {
                throw new RuntimeException(e);
            }
        }
        updateWellbeingData();
    }

    private SystemShortcut getShortcutForApp(String packageName, int userId, BaseDraggingActivity activity, ItemInfo info) {
        Preconditions.assertUIThread();
        if (userId != UserHandle.myUserId()) {
            if (this.mIsInTest) {
                Log.d(TAG, "getShortcutForApp [" + packageName + "]: not current user");
            }
            return null;
        }
        synchronized (this.mModelLock) {
            String str = this.mPackageToActionId.get(packageName);
            RemoteAction remoteAction = str != null ? this.mActionIdMap.get(str) : null;
            if (remoteAction == null) {
                if (this.mIsInTest) {
                    Log.d(TAG, "getShortcutForApp [" + packageName + "]: no action");
                }
                return null;
            }
            if (this.mIsInTest) {
                Log.d(TAG, "getShortcutForApp [" + packageName + "]: action: '" + ((Object) remoteAction.getTitle()) + "'");
            }
            return new RemoteActionShortcut(remoteAction, activity, info);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateWellbeingData() {
        this.mWorkerHandler.sendEmptyMessage(3);
    }

    private Uri.Builder apiBuilder() {
        return new Uri.Builder().scheme("content").authority(this.mWellbeingProviderPkg + ".api");
    }

    private boolean updateActions(String... packageNames) {
        if (packageNames.length == 0) {
            return true;
        }
        if (this.mIsInTest) {
            Log.d(TAG, "retrieveActions() called with: packageNames = [" + String.join(", ", packageNames) + "]");
        }
        Preconditions.assertNonUiThread();
        Uri uriBuild = apiBuilder().build();
        try {
            ContentProviderClient contentProviderClientAcquireUnstableContentProviderClient = this.mContext.getContentResolver().acquireUnstableContentProviderClient(uriBuild);
            try {
                if (contentProviderClientAcquireUnstableContentProviderClient == null) {
                    if (this.mIsInTest) {
                        Log.i(TAG, "retrieveActions(): null provider");
                    }
                    if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                        contentProviderClientAcquireUnstableContentProviderClient.close();
                    }
                    return false;
                }
                Bundle bundle = new Bundle();
                bundle.putStringArray(EXTRA_PACKAGES, packageNames);
                bundle.putInt(EXTRA_MAX_NUM_ACTIONS_SHOWN, 1);
                Bundle bundleCall = contentProviderClientAcquireUnstableContentProviderClient.call(METHOD_GET_ACTIONS, null, bundle);
                if (!bundleCall.getBoolean(EXTRA_SUCCESS, true)) {
                    if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                        contentProviderClientAcquireUnstableContentProviderClient.close();
                    }
                    return false;
                }
                synchronized (this.mModelLock) {
                    Stream stream = Arrays.stream(packageNames);
                    final Map<String, String> map = this.mPackageToActionId;
                    Objects.requireNonNull(map);
                    stream.forEach(new Consumer() { // from class: com.android.launcher3.model.-$$Lambda$ExrpFDpa6lNwbujTZYPUBSDeDQI
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            map.remove((String) obj);
                        }
                    });
                    for (String str : bundleCall.getStringArray(EXTRA_ACTIONS)) {
                        Bundle bundle2 = bundleCall.getBundle(str);
                        this.mActionIdMap.put(str, (RemoteAction) bundle2.getParcelable("action"));
                        String[] stringArray = bundle2.getStringArray(EXTRA_PACKAGES);
                        if (this.mIsInTest) {
                            Log.d(TAG, "....actionId: " + str + ", packages: " + String.join(", ", stringArray));
                        }
                        for (String str2 : stringArray) {
                            this.mPackageToActionId.put(str2, str);
                        }
                    }
                }
                if (contentProviderClientAcquireUnstableContentProviderClient != null) {
                    contentProviderClientAcquireUnstableContentProviderClient.close();
                }
                if (this.mIsInTest) {
                    Log.i(TAG, "retrieveActions(): finished");
                }
                return true;
            } finally {
            }
        } catch (DeadObjectException unused) {
            Log.i(TAG, "retrieveActions(): DeadObjectException");
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve data from " + uriBuild + ": " + e);
            if (this.mIsInTest) {
                throw new RuntimeException(e);
            }
            return true;
        }
        Log.i(TAG, "retrieveActions(): DeadObjectException");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean handleMessage(Message msg) {
        int i = msg.what;
        if (i == 1) {
            String str = (String) msg.obj;
            this.mWorkerHandler.removeCallbacksAndMessages(str);
            if (!updateActions(str)) {
                scheduleRefreshRetry(msg);
            }
            return true;
        }
        if (i != 2) {
            if (i != 3) {
                return false;
            }
            this.mWorkerHandler.removeCallbacksAndMessages(null);
            if (!updateActions((String[]) ((LauncherApps) this.mContext.getSystemService(LauncherApps.class)).getActivityList(null, Process.myUserHandle()).stream().map(new Function() { // from class: com.android.launcher3.model.-$$Lambda$WellbeingModel$SBLVGBGBGap5GgxBV0v4v_IqlMA
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((LauncherActivityInfo) obj).getApplicationInfo().packageName;
                }
            }).distinct().toArray(new IntFunction() { // from class: com.android.launcher3.model.-$$Lambda$WellbeingModel$wbvdoEXPkRpf-0xNzHz7tb8qkrk
                @Override // java.util.function.IntFunction
                public final Object apply(int i2) {
                    return WellbeingModel.lambda$handleMessage$1(i2);
                }
            }))) {
                scheduleRefreshRetry(msg);
            }
            return true;
        }
        String str2 = (String) msg.obj;
        this.mWorkerHandler.removeCallbacksAndMessages(str2);
        synchronized (this.mModelLock) {
            this.mPackageToActionId.remove(str2);
        }
        return true;
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: NEW_ARRAY (r0v0 int A[IMMUTABLE_TYPE]) (LINE:287) type: java.lang.String[] */
    static /* synthetic */ String[] lambda$handleMessage$1(int i) {
        return new String[i];
    }

    private void scheduleRefreshRetry(Message originalMsg) {
        int i = originalMsg.arg1;
        if (i >= RETRY_TIMES_MS.length) {
            return;
        }
        Message messageObtain = Message.obtain(originalMsg);
        messageObtain.arg1 = i + 1;
        this.mWorkerHandler.sendMessageDelayed(messageObtain, r1[i]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAppPackageChanged(Intent intent) {
        if (this.mIsInTest) {
            Log.d(TAG, "Changes in apps: intent = [" + intent + "]");
        }
        Preconditions.assertUIThread();
        String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
        if (schemeSpecificPart == null || schemeSpecificPart.length() == 0) {
            return;
        }
        String action = intent.getAction();
        if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
            Message.obtain(this.mWorkerHandler, 2, schemeSpecificPart).sendToTarget();
        } else if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
            Message.obtain(this.mWorkerHandler, 1, schemeSpecificPart).sendToTarget();
        }
    }

    static /* synthetic */ SystemShortcut lambda$static$2(BaseDraggingActivity baseDraggingActivity, ItemInfo itemInfo) {
        if (itemInfo.getTargetComponent() == null) {
            return null;
        }
        return INSTANCE.lambda$get$0$MainThreadInitializedObject(baseDraggingActivity).getShortcutForApp(itemInfo.getTargetComponent().getPackageName(), itemInfo.user.getIdentifier(), baseDraggingActivity, itemInfo);
    }
}
