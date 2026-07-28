package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Process;
import com.lge.launcher3.liveicon.AlarmClockLiveIcon;
import com.lge.launcher3.liveicon.LiveIconManager;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class IconProvider {
    private static final boolean DBG = false;
    private static final String TAG = "IconProvider";
    protected String mSystemState;

    public IconProvider() {
        updateSystemStateString();
    }

    public void updateSystemStateString() {
        this.mSystemState = Locale.getDefault().toString();
    }

    public String getIconSystemState(String packageName) {
        return this.mSystemState;
    }

    public Drawable getIcon(LauncherActivityInfo info, int iconDpi, Context context) {
        ComponentName componentName = info.getComponentName();
        LiveIconManager liveIconManager = LiveIconManager.getInstance(context);
        Drawable badgedIcon = liveIconManager.hasLiveIcon(componentName) ? liveIconManager.getBadgedIcon(componentName, Process.myUserHandle()) : null;
        return badgedIcon == null ? info.getBadgedIcon(iconDpi) : badgedIcon;
    }

    public Drawable getSwivelIcon(LauncherActivityInfo info, int iconDpi, Context context) {
        Drawable badgedIcon = null;
        if (info == null) {
            return null;
        }
        ComponentName componentName = info.getComponentName();
        LiveIconManager liveIconManager = LiveIconManager.getInstance(context);
        if (liveIconManager.hasLiveIcon(componentName)) {
            if (componentName.equals(ComponentName.unflattenFromString(AlarmClockLiveIcon.ACTIVITY_COMPONENT_NAME))) {
                badgedIcon = liveIconManager.getSwivelAlarmClockIcon(Process.myUserHandle());
            } else {
                badgedIcon = liveIconManager.getBadgedIcon(componentName, Process.myUserHandle());
            }
        }
        return badgedIcon == null ? info.getBadgedIcon(iconDpi) : badgedIcon;
    }
}
