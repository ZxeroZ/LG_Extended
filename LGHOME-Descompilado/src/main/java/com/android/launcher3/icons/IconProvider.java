package com.android.launcher3.icons;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import com.android.launcher3.pm.UserCache;
import com.android.launcher3.util.SafeCloseable;
import com.lge.launcher3.liveicon.AlarmClockLiveIcon;
import com.lge.launcher3.liveicon.LiveIconManager;
import java.util.Iterator;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/* JADX INFO: loaded from: classes.dex */
public class IconProvider {
    private static final boolean DBG = false;
    private static final int NO_ID = 0;
    private static final String SYSTEM_STATE_SEPARATOR = " ";
    private static final String TAG = "IconProvider";
    protected String mSystemState;
    private static final BiFunction<LauncherActivityInfo, Integer, Drawable> LAI_LOADER = new BiFunction() { // from class: com.android.launcher3.icons.-$$Lambda$IconProvider$kDeKlXEqwCSg0cJH6YaCv-wvk3c
        @Override // java.util.function.BiFunction
        public final Object apply(Object obj, Object obj2) {
            return ((LauncherActivityInfo) obj).getIcon(((Integer) obj2).intValue());
        }
    };
    private static final BiFunction<ActivityInfo, PackageManager, Drawable> AI_LOADER = new BiFunction() { // from class: com.android.launcher3.icons.-$$Lambda$IconProvider$j6fn6Y4jeSRkSKqcr5qAQ6LYA_I
        @Override // java.util.function.BiFunction
        public final Object apply(Object obj, Object obj2) {
            return ((ActivityInfo) obj).loadUnbadgedIcon((PackageManager) obj2);
        }
    };

    static /* synthetic */ void lambda$registerIconChangeListener$0() {
    }

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

    public Drawable getIcon(ActivityInfo info, UserHandle user, Context context) {
        return getIcon(info.applicationInfo.packageName, user, info, context.getPackageManager(), AI_LOADER);
    }

    private <T, P> Drawable getIcon(String packageName, UserHandle user, T obj, P param, BiFunction<T, P, Drawable> loader) {
        return loader.apply(obj, param);
    }

    public static SafeCloseable registerIconChangeListener(final Context context, BiConsumer<String, UserHandle> callback, Handler handler) {
        ComponentName componentOrNull = parseComponentOrNull(context, com.lge.launcher3.R.string.calendar_component_name);
        ComponentName componentOrNull2 = parseComponentOrNull(context, com.lge.launcher3.R.string.clock_component_name);
        if (componentOrNull == null && componentOrNull2 == null) {
            return new SafeCloseable() { // from class: com.android.launcher3.icons.-$$Lambda$IconProvider$QD0GUo-r6kdqyzaepTvoExu-Msw
                @Override // com.android.launcher3.util.SafeCloseable, java.lang.AutoCloseable
                public final void close() {
                    IconProvider.lambda$registerIconChangeListener$0();
                }
            };
        }
        final DateTimeChangeReceiver dateTimeChangeReceiver = new DateTimeChangeReceiver(callback);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.TIMEZONE_CHANGED");
        if (componentOrNull != null) {
            intentFilter.addAction("android.intent.action.TIME_SET");
            intentFilter.addAction("android.intent.action.DATE_CHANGED");
        }
        context.registerReceiver(dateTimeChangeReceiver, intentFilter, null, handler);
        return new SafeCloseable() { // from class: com.android.launcher3.icons.-$$Lambda$IconProvider$i52KO9DWG8Y4h3C42a-EhHhe_EU
            @Override // com.android.launcher3.util.SafeCloseable, java.lang.AutoCloseable
            public final void close() {
                context.unregisterReceiver(dateTimeChangeReceiver);
            }
        };
    }

    private static class DateTimeChangeReceiver extends BroadcastReceiver {
        private final BiConsumer<String, UserHandle> mCallback;

        DateTimeChangeReceiver(BiConsumer<String, UserHandle> callback) {
            this.mCallback = callback;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            ComponentName componentOrNull;
            if ("android.intent.action.TIMEZONE_CHANGED".equals(intent.getAction()) && (componentOrNull = IconProvider.parseComponentOrNull(context, com.lge.launcher3.R.string.clock_component_name)) != null) {
                this.mCallback.accept(componentOrNull.getPackageName(), Process.myUserHandle());
            }
            ComponentName componentOrNull2 = IconProvider.parseComponentOrNull(context, com.lge.launcher3.R.string.calendar_component_name);
            if (componentOrNull2 != null) {
                Iterator<UserHandle> it = UserCache.INSTANCE.lambda$get$0$MainThreadInitializedObject(context).getUserProfiles().iterator();
                while (it.hasNext()) {
                    this.mCallback.accept(componentOrNull2.getPackageName(), it.next());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ComponentName parseComponentOrNull(Context context, int resId) {
        String string = context.getString(resId);
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        return ComponentName.unflattenFromString(string);
    }

    public Drawable getSwivelIcon(LauncherActivityInfo info, int iconDpi, Context context) {
        Drawable badgedIcon;
        ComponentName componentName = info.getComponentName();
        LiveIconManager liveIconManager = LiveIconManager.getInstance(context);
        if (!liveIconManager.hasLiveIcon(componentName)) {
            badgedIcon = null;
        } else if (componentName.equals(ComponentName.unflattenFromString(AlarmClockLiveIcon.ACTIVITY_COMPONENT_NAME))) {
            badgedIcon = liveIconManager.getSwivelAlarmClockIcon(Process.myUserHandle());
        } else {
            badgedIcon = liveIconManager.getBadgedIcon(componentName, Process.myUserHandle());
        }
        return badgedIcon == null ? info.getIcon(iconDpi) : badgedIcon;
    }
}
