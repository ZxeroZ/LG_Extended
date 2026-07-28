package com.lge.launcher3.liveicon;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.android.launcher3.FastBitmapDrawable;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class AlarmClockLiveIcon extends LiveIcon {
    public static final String ACTIVITY_COMPONENT_NAME = "com.lge.clock/com.lge.clock.AlarmClockActivity";
    private static final String TAG = "LiveIcon.AlarmClock";
    protected ComponentName mActivityComponentName;
    protected AnalogClockView mAnalogClockView;
    protected Context mContext;
    protected Bitmap mIcon;
    protected Drawable mIconForAdaptiveIcon;

    public String toString() {
        return TAG;
    }

    public AlarmClockLiveIcon(Context context) {
        super(context);
        this.mContext = context;
        this.mActivityComponentName = ComponentName.unflattenFromString(ACTIVITY_COMPONENT_NAME);
        this.mAnalogClockView = (AnalogClockView) LayoutInflater.from(context).inflate(R.layout.liveicon_alarmclock, (ViewGroup) null);
        updateIconImpl();
    }

    @Override // com.lge.launcher3.liveicon.LiveIcon
    public ComponentName getComponentName() {
        return this.mActivityComponentName;
    }

    @Override // com.lge.launcher3.liveicon.LiveIcon
    protected Drawable getIcon() {
        if (LGHomeFeature.Config.FEATURE_SUPPORT_ICON_FRAMES.getValue()) {
            return this.mIconForAdaptiveIcon;
        }
        if (this.mIcon != null) {
            return new FastBitmapDrawable(this.mIcon);
        }
        return null;
    }

    @Override // com.lge.launcher3.liveicon.LiveIcon
    protected void updateIconImpl() {
        this.mAnalogClockView.update();
        this.mIcon = Utilities.loadBitmapFromView(this.mAnalogClockView, true);
        if (LGHomeFeature.Config.FEATURE_SUPPORT_ICON_FRAMES.getValue()) {
            int identifier = this.mContext.getResources().getIdentifier("lg_iconframe_clock_home", LauncherConst.RESOURCE_IMAGE_TYPE, this.mContext.getPackageName());
            if (this.mIcon != null) {
                this.mIconForAdaptiveIcon = DDTUtils.convertToCushionIcon(this.mContext, new AdaptiveIconDrawable(new FastBitmapDrawable(this.mIcon), null), this.mContext.getPackageName(), identifier);
                this.mIcon = null;
            } else {
                LGLog.i(TAG, "AlarmClock icon is null");
            }
        }
    }

    @Override // com.lge.launcher3.liveicon.LiveIcon
    protected boolean shouldUpdate() {
        return this.mAnalogClockView.shouldUpdate();
    }

    @Override // com.lge.launcher3.liveicon.LiveIcon
    protected IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        return intentFilter;
    }

    @Override // com.lge.launcher3.liveicon.LiveIcon
    public void startEventListening() {
        super.startEventListening();
        startTimeTickUpdate();
    }

    @Override // com.lge.launcher3.liveicon.LiveIcon
    public void stopEventListening() {
        super.stopEventListening();
        stopTimeTickUpdate();
    }
}
