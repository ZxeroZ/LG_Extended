package com.android.quickstep;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Process;
import android.util.SparseIntArray;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.SimpleBroadcastReceiver;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.lge.launcher3.quickstep.ActivityManagerWrapperEx;
import com.lge.launcher3.util.PackageUtils;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public final class OverviewComponentObserver {
    private BaseActivityInterface mActivityInterface;
    private BaseActivityInterface mActivityInterfaceForMulti;
    private final SparseIntArray mConfigChangesMap;
    private final Context mContext;
    private final Intent mCurrentHomeIntent;
    private final RecentsAnimationDeviceState mDeviceState;
    private final Intent mFallbackIntent;
    private boolean mIsDefaultHome;
    private boolean mIsHomeAndOverviewSame;
    private boolean mIsHomeDisabled;
    private final Intent mMyHomeIntent;
    private Consumer<Boolean> mOverviewChangeListener;
    private Intent mOverviewIntent;
    private String mUpdateRegisteredPackage;
    private final BroadcastReceiver mUserPreferenceChangeReceiver = new SimpleBroadcastReceiver(new Consumer() { // from class: com.android.quickstep.-$$Lambda$OverviewComponentObserver$ZHvBON2wUbzSNCxS2lkv_MLFFLk
        @Override // java.util.function.Consumer
        public final void accept(Object obj) {
            this.f$0.updateOverviewTargets((Intent) obj);
        }
    });
    private final BroadcastReceiver mOtherHomeAppUpdateReceiver = new SimpleBroadcastReceiver(new Consumer() { // from class: com.android.quickstep.-$$Lambda$OverviewComponentObserver$ZHvBON2wUbzSNCxS2lkv_MLFFLk
        @Override // java.util.function.Consumer
        public final void accept(Object obj) {
            this.f$0.updateOverviewTargets((Intent) obj);
        }
    });

    static /* synthetic */ void lambda$new$0(Boolean bool) {
    }

    public OverviewComponentObserver(Context context, RecentsAnimationDeviceState deviceState) {
        SparseIntArray sparseIntArray = new SparseIntArray();
        this.mConfigChangesMap = sparseIntArray;
        this.mOverviewChangeListener = new Consumer() { // from class: com.android.quickstep.-$$Lambda$OverviewComponentObserver$hExlPGd0G-M-pQayNe2n8V6Ff14
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                OverviewComponentObserver.lambda$new$0((Boolean) obj);
            }
        };
        this.mContext = context;
        this.mDeviceState = deviceState;
        Intent flags = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory(PackageUtils.ANDROID_INTENT_CATEGORY_HOME).setFlags(268435456);
        this.mCurrentHomeIntent = flags;
        Intent intent = new Intent(flags).setPackage(context.getPackageName());
        this.mMyHomeIntent = intent;
        ResolveInfo resolveInfoResolveActivity = context.getPackageManager().resolveActivity(intent, 0);
        ComponentName componentName = new ComponentName(context.getPackageName(), resolveInfoResolveActivity.activityInfo.name);
        intent.setComponent(componentName);
        sparseIntArray.append(componentName.hashCode(), resolveInfoResolveActivity.activityInfo.configChanges);
        ComponentName componentName2 = new ComponentName(context, (Class<?>) RecentsActivity.class);
        Intent flags2 = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory("android.intent.category.DEFAULT").setComponent(componentName2).setFlags(268435456);
        this.mFallbackIntent = flags2;
        try {
            sparseIntArray.append(componentName2.hashCode(), context.getPackageManager().getActivityInfo(flags2.getComponent(), 0).configChanges);
        } catch (PackageManager.NameNotFoundException unused) {
        }
        this.mContext.registerReceiver(this.mUserPreferenceChangeReceiver, new IntentFilter(PackageManagerWrapper.ACTION_PREFERRED_ACTIVITY_CHANGED));
        updateOverviewTargets();
    }

    public void setOverviewChangeListener(Consumer<Boolean> overviewChangeListener) {
        this.mOverviewChangeListener = overviewChangeListener;
    }

    public void onSystemUiStateChanged() {
        if (this.mDeviceState.isHomeDisabled() != this.mIsHomeDisabled) {
            updateOverviewTargets();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateOverviewTargets(Intent unused) {
        updateOverviewTargets();
    }

    public boolean assistantGestureIsConstrained() {
        return (this.mDeviceState.getSystemUiStateFlags() & 8192) != 0;
    }

    private void updateOverviewTargets() {
        ComponentName homeActivities = PackageManagerWrapper.getInstance().getHomeActivities(new ArrayList());
        this.mIsHomeDisabled = this.mDeviceState.isHomeDisabled();
        this.mIsDefaultHome = Objects.equals(this.mMyHomeIntent.getComponent(), homeActivities);
        BaseActivityInterface baseActivityInterface = this.mActivityInterface;
        if (baseActivityInterface != null) {
            baseActivityInterface.onAssistantVisibilityChanged(0.0f);
        }
        if (FeatureFlags.SEPARATE_RECENTS_ACTIVITY.get()) {
            this.mIsDefaultHome = false;
            if (homeActivities == null) {
                homeActivities = this.mMyHomeIntent.getComponent();
            }
        }
        if (!this.mDeviceState.isHomeDisabled() && (homeActivities == null || this.mIsDefaultHome)) {
            this.mActivityInterface = LauncherActivityInterface.INSTANCE;
            this.mIsHomeAndOverviewSame = true;
            Intent intent = this.mMyHomeIntent;
            this.mOverviewIntent = intent;
            this.mCurrentHomeIntent.setComponent(intent.getComponent());
            unregisterOtherHomeAppUpdateReceiver();
        } else {
            this.mActivityInterface = FallbackActivityInterface.INSTANCE;
            this.mIsHomeAndOverviewSame = false;
            this.mOverviewIntent = this.mFallbackIntent;
            this.mCurrentHomeIntent.setComponent(homeActivities);
            if (homeActivities == null) {
                unregisterOtherHomeAppUpdateReceiver();
            } else if (!homeActivities.getPackageName().equals(this.mUpdateRegisteredPackage)) {
                unregisterOtherHomeAppUpdateReceiver();
                String packageName = homeActivities.getPackageName();
                this.mUpdateRegisteredPackage = packageName;
                this.mContext.registerReceiver(this.mOtherHomeAppUpdateReceiver, PackageManagerHelper.getPackageFilter(packageName, "android.intent.action.PACKAGE_ADDED", "android.intent.action.PACKAGE_CHANGED", "android.intent.action.PACKAGE_REMOVED"));
            }
        }
        this.mActivityInterfaceForMulti = FallbackActivityInterface.INSTANCE;
        this.mOverviewChangeListener.accept(Boolean.valueOf(this.mIsHomeAndOverviewSame));
    }

    public void onDestroy() {
        this.mContext.unregisterReceiver(this.mUserPreferenceChangeReceiver);
        unregisterOtherHomeAppUpdateReceiver();
    }

    private void unregisterOtherHomeAppUpdateReceiver() {
        if (this.mUpdateRegisteredPackage != null) {
            this.mContext.unregisterReceiver(this.mOtherHomeAppUpdateReceiver);
            this.mUpdateRegisteredPackage = null;
        }
    }

    boolean canHandleConfigChanges(ComponentName component, int changes) {
        if ((changes & 1152) == 1152) {
            return true;
        }
        int i = this.mConfigChangesMap.get(component.hashCode());
        return i != 0 && ((~i) & changes) == 0;
    }

    Intent getOverviewIntentIgnoreSysUiState() {
        return this.mIsDefaultHome ? this.mMyHomeIntent : this.mOverviewIntent;
    }

    public Intent getOverviewIntent() {
        return this.mOverviewIntent;
    }

    public Intent getOverviewIntent(int displayId) {
        if (displayId == 0) {
            return this.mOverviewIntent;
        }
        return this.mFallbackIntent;
    }

    public Intent getHomeIntent() {
        return this.mCurrentHomeIntent;
    }

    public Intent getHomeIntent(int displayId) {
        if (displayId == 0) {
            return this.mCurrentHomeIntent;
        }
        return ActivityManagerWrapperEx.getInstance().getSecondaryHomeIntent(Process.myUserHandle().getIdentifier(), displayId).addCategory("android.intent.category.SECONDARY_HOME").addFlags(268435456);
    }

    public boolean isHomeAndOverviewSame() {
        return this.mIsHomeAndOverviewSame;
    }

    public BaseActivityInterface getActivityInterface() {
        return this.mActivityInterface;
    }

    public BaseActivityInterface getActivityInterface(int displayId) {
        if (displayId == 0) {
            return this.mActivityInterface;
        }
        return this.mActivityInterfaceForMulti;
    }

    public void dump(PrintWriter pw) {
        pw.println("OverviewComponentObserver:");
        pw.println("  isDefaultHome=" + this.mIsDefaultHome);
        pw.println("  isHomeDisabled=" + this.mIsHomeDisabled);
        pw.println("  homeAndOverviewSame=" + this.mIsHomeAndOverviewSame);
        pw.println("  overviewIntent=" + this.mOverviewIntent);
        pw.println("  homeIntent=" + this.mCurrentHomeIntent);
    }
}
