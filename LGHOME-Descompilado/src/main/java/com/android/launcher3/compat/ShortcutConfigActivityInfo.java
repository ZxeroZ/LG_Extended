package com.android.launcher3.compat;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import android.widget.Toast;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.icons.IconCache;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public abstract class ShortcutConfigActivityInfo {
    private static final String TAG = "SCActivityInfo";
    private final ComponentName mCn;
    private final UserHandle mUser;

    public ShortcutInfo createShortcutInfo() {
        return null;
    }

    public abstract Drawable getFullResIcon(IconCache cache);

    public int getItemType() {
        return 1;
    }

    public abstract CharSequence getLabel();

    public boolean isPersistable() {
        return true;
    }

    protected ShortcutConfigActivityInfo(ComponentName cn, UserHandle user) {
        this.mCn = cn;
        this.mUser = user;
    }

    public ComponentName getComponent() {
        return this.mCn;
    }

    public UserHandle getUser() {
        return this.mUser;
    }

    public boolean startConfigActivity(Activity activity, int requestCode) {
        Intent component = new Intent("android.intent.action.CREATE_SHORTCUT").setComponent(getComponent());
        try {
            activity.startActivityForResult(component, requestCode);
            return true;
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(activity, R.string.activity_not_found, 0).show();
            return false;
        } catch (SecurityException e) {
            Toast.makeText(activity, R.string.activity_not_found, 0).show();
            Log.e(TAG, "Launcher does not have the permission to launch " + component + ". Make sure to create a MAIN intent-filter for the corresponding activity or use the exported attribute for this activity.", e);
            return false;
        }
    }

    static class ShortcutConfigActivityInfoVL extends ShortcutConfigActivityInfo {
        private final ActivityInfo mInfo;
        private final PackageManager mPm;

        public ShortcutConfigActivityInfoVL(ActivityInfo info, PackageManager pm) {
            super(new ComponentName(info.packageName, info.name), Process.myUserHandle());
            this.mInfo = info;
            this.mPm = pm;
        }

        @Override // com.android.launcher3.compat.ShortcutConfigActivityInfo
        public CharSequence getLabel() {
            return this.mInfo.loadLabel(this.mPm);
        }

        @Override // com.android.launcher3.compat.ShortcutConfigActivityInfo
        public Drawable getFullResIcon(IconCache cache) {
            return cache.getFullResIcon(this.mInfo);
        }
    }

    static class ShortcutConfigActivityInfoVO extends ShortcutConfigActivityInfo {
        private final LauncherActivityInfo mInfo;

        public ShortcutConfigActivityInfoVO(LauncherActivityInfo info) {
            super(info.getComponentName(), info.getUser());
            this.mInfo = info;
        }

        @Override // com.android.launcher3.compat.ShortcutConfigActivityInfo
        public CharSequence getLabel() {
            return this.mInfo.getLabel();
        }

        @Override // com.android.launcher3.compat.ShortcutConfigActivityInfo
        public Drawable getFullResIcon(IconCache cache) {
            return cache.getFullResIcon(this.mInfo);
        }

        @Override // com.android.launcher3.compat.ShortcutConfigActivityInfo
        public boolean startConfigActivity(Activity activity, int requestCode) {
            if (getUser().equals(Process.myUserHandle())) {
                return super.startConfigActivity(activity, requestCode);
            }
            try {
                activity.startIntentSenderForResult((IntentSender) LauncherApps.class.getDeclaredMethod("getShortcutConfigActivityIntent", LauncherActivityInfo.class).invoke(activity.getSystemService(LauncherApps.class), this.mInfo), requestCode, null, 0, 0, 0);
                return true;
            } catch (Exception e) {
                Log.e(ShortcutConfigActivityInfo.TAG, "Error calling new API", e);
                return false;
            }
        }
    }
}
