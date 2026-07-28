package com.lge.launcher3.liveicon;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.util.Log;
import com.lge.content.pm.PackageManagerEx;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/* JADX INFO: loaded from: classes.dex */
public class LiveIconManager implements Observer {
    private static final String TAG = "LiveIcon";
    private static LiveIconManager sInstance;
    private Context mContext;
    private ArrayList<LiveIcon> mLiveIcons = new ArrayList<>();
    private boolean mEnabled = true;
    private boolean mStarted = false;
    private List<OnLiveIconUpdateListener> mUpdateListeners = new ArrayList();

    public static LiveIconManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LiveIconManager(context);
        }
        return sInstance;
    }

    private LiveIconManager(Context context) {
        this.mContext = context.getApplicationContext();
        updateEnabled();
    }

    public void updateEnabled() {
        boolean z = (DDTUtils.isAdditionalThemeApplied(this.mContext) || DDTUtils.isAdditionalIconThemeApplied(this.mContext)) ? false : true;
        this.mEnabled = z;
        LGLog.i(TAG, String.format("updateEnabled() : %s", Boolean.valueOf(z)));
        if (this.mEnabled) {
            addLiveIcons();
        } else {
            removeLiveIcons();
        }
    }

    private void addLiveIcons() {
        if (this.mLiveIcons.isEmpty()) {
            this.mLiveIcons.add(new CalendarLiveIcon(this.mContext));
            this.mLiveIcons.add(new AlarmClockLiveIcon(this.mContext));
            if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
                this.mLiveIcons.add(new SwivelAlarmClockLiveIcon(this.mContext));
            }
            Iterator<LiveIcon> it = this.mLiveIcons.iterator();
            while (it.hasNext()) {
                it.next().addObserver(this);
            }
        }
    }

    private void removeLiveIcons() {
        if (this.mLiveIcons.isEmpty()) {
            return;
        }
        Iterator<LiveIcon> it = this.mLiveIcons.iterator();
        while (it.hasNext()) {
            it.next().deleteObserver(this);
        }
        this.mLiveIcons.clear();
    }

    public void start() {
        if (this.mEnabled && !this.mStarted) {
            LGLog.d(TAG, "Start event listening");
            for (LiveIcon liveIcon : this.mLiveIcons) {
                liveIcon.startEventListening();
                liveIcon.updateIcon();
            }
            this.mStarted = true;
        }
    }

    public void stop() {
        if (this.mEnabled && this.mStarted) {
            LGLog.d(TAG, "Stop event listening");
            Iterator<LiveIcon> it = this.mLiveIcons.iterator();
            while (it.hasNext()) {
                it.next().stopEventListening();
            }
            this.mStarted = false;
        }
    }

    @Override // java.util.Observer
    public void update(final Observable observable, Object data) {
        if (this.mEnabled) {
            LGLog.d(TAG, "Update live icon: " + observable);
            Iterator<OnLiveIconUpdateListener> it = this.mUpdateListeners.iterator();
            while (it.hasNext()) {
                it.next().onLiveIconUpdate((LiveIcon) observable);
            }
        }
    }

    public boolean hasLiveIcon(ComponentName name) {
        return get(name) != null;
    }

    private LiveIcon get(ComponentName component) {
        if (component == null) {
            return null;
        }
        for (LiveIcon liveIcon : this.mLiveIcons) {
            if (!(liveIcon instanceof SwivelAlarmClockLiveIcon) && component.equals(liveIcon.getComponentName())) {
                return liveIcon;
            }
        }
        return null;
    }

    public Drawable getBadgedIcon(ComponentName name, UserHandle user) {
        LiveIcon liveIcon = get(name);
        if (liveIcon != null) {
            return liveIcon.getBadgedIcon(user);
        }
        return null;
    }

    public Drawable getSwivelAlarmClockIcon(UserHandle user) {
        for (LiveIcon liveIcon : this.mLiveIcons) {
            if (liveIcon instanceof SwivelAlarmClockLiveIcon) {
                return liveIcon.getBadgedIcon(user);
            }
        }
        return null;
    }

    public void registerOnLiveIconUpdateListener(OnLiveIconUpdateListener listener) {
        if (this.mUpdateListeners.contains(listener)) {
            return;
        }
        this.mUpdateListeners.add(listener);
    }

    public void unregisterOnLiveIconUpdateListener(OnLiveIconUpdateListener listener) {
        this.mUpdateListeners.remove(listener);
    }

    public Drawable getDefaultIconOfLiveIcon(Context context, ComponentName componentName) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(componentName.getPackageName(), 0);
            return PackageManagerEx.getDefault().loadUnbadgedFramedIcon(context, applicationInfo, applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Can not bring default application icon of LiveIcon.");
            e.printStackTrace();
            return null;
        }
    }

    public void setForceUpdate() {
        Iterator<LiveIcon> it = this.mLiveIcons.iterator();
        while (it.hasNext()) {
            it.next().setForceUpdate();
        }
    }
}
