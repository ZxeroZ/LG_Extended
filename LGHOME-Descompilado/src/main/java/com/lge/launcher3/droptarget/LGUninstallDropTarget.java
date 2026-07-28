package com.lge.launcher3.droptarget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.Toast;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.UninstallDropTarget;
import com.android.launcher3.allapps.AllAppsList;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.ManagedProfileUtils;

/* JADX INFO: loaded from: classes.dex */
public class LGUninstallDropTarget extends UninstallDropTarget {
    private static final int REQUEST_CODE_LG_UNINSTALL_ACTIVITY = 0;
    private boolean mIsRemove;

    public LGUninstallDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LGUninstallDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mIsRemove = false;
    }

    @Override // com.android.launcher3.UninstallDropTarget, com.android.launcher3.ButtonDropTarget
    protected void completeDrop(final DropTarget.DragObject d) {
        final Pair<ComponentName, Integer> appInfoFlags = getAppInfoFlags(d.dragInfo);
        final UserHandle userHandle = ((ItemInfo) d.dragInfo).user;
        this.mIsRemove = false;
        if (startUninstallActivityForResult(this.mLauncher, d.dragInfo)) {
            this.mLauncher.addOnResumeCallback(new Runnable() { // from class: com.lge.launcher3.droptarget.-$$Lambda$LGUninstallDropTarget$fTFaGbxnzwraCfo5JBWLRmQM-SI
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$completeDrop$0$LGUninstallDropTarget(d);
                }
            });
            this.mLauncher.addOnResumeCallback(new Runnable() { // from class: com.lge.launcher3.droptarget.-$$Lambda$LGUninstallDropTarget$Ju4U6ClUZXmng_2ObCbvnVYacf0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$completeDrop$1$LGUninstallDropTarget(appInfoFlags, userHandle, d);
                }
            });
        } else {
            sendUninstallResult(d.dragSource, false);
        }
    }

    public /* synthetic */ void lambda$completeDrop$0$LGUninstallDropTarget(DropTarget.DragObject dragObject) {
        startReinstallerActivity(((ItemInfo) dragObject.dragInfo).title.toString());
    }

    public /* synthetic */ void lambda$completeDrop$1$LGUninstallDropTarget(Pair pair, UserHandle userHandle, DropTarget.DragObject dragObject) {
        sendUninstallResult(dragObject.dragSource, !AllAppsList.packageHasActivities(getContext(), ((ComponentName) pair.first).getPackageName(), userHandle));
    }

    public boolean startUninstallActivityForResult(Launcher launcher, Object info) {
        if (ManagedProfileUtils.hasDeviceOwner(launcher) || ManagedProfileUtils.hasProfileOwner(launcher)) {
            return startUninstallActivity(launcher, info);
        }
        Pair<ComponentName, Integer> appInfoFlags = getAppInfoFlags(info);
        UserHandle userHandle = ((ItemInfo) info).user;
        ComponentName componentName = (ComponentName) appInfoFlags.first;
        if ((((Integer) appInfoFlags.second).intValue() & 1) == 0) {
            Toast.makeText(launcher, R.string.uninstall_system_app_text, 0).show();
            return false;
        }
        Intent intent = new Intent(IntentConst.Action.ACTION_SHOW_DELETE_DIALOG.getValue(launcher.getBaseContext()), Uri.fromParts(AppNotifierManager.ExtraSpec.USAGE_PACKAGE, componentName.getPackageName(), componentName.getClassName()));
        intent.setFlags(545259520);
        if (userHandle != null) {
            intent.putExtra("android.intent.extra.USER", userHandle);
        }
        startActivityForResult(intent, 0);
        return true;
    }

    private void startReinstallerActivity(String appName) {
        if (LGHomeFeature.Config.FEATURE_USE_RECENT_UNINSTALL_APP.getValue() && !isCheckedReintallerDoNotShowAgain() && this.mIsRemove) {
            Intent intent = new Intent(IntentConst.Action.ACTION_SHOW_REINSTALL_DIALOG.getValue(getContext()));
            intent.putExtra(LauncherConst.EXTRA_APP_NAME, appName);
            getContext().startActivity(intent);
        }
    }

    private boolean isCheckedReintallerDoNotShowAgain() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(ReinstallerActivity.PREF_DO_NOT_SHOW_AGAIN, 0);
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(ReinstallerActivity.PREF_KEY_REINSTALL, false);
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0 && resultCode == -1) {
            this.mIsRemove = true;
        }
    }
}
